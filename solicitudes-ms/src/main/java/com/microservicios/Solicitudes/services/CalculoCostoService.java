package com.microservicios.Solicitudes.services;

import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.client.TransporteServiceClient;
import com.microservicios.Solicitudes.client.UbicacionesServiceClient;
import com.microservicios.Solicitudes.dto.external.CamionDTO;
import com.microservicios.Solicitudes.dto.external.DepositoDTO;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.Tarifa;
import com.microservicios.Solicitudes.entity.Tramo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CalculoCostoService {

    private final TarifaService tarifaService;
    private final TransporteServiceClient camionClient; // lo dejamos listo
    private final UbicacionesServiceClient depositoClient; // si aún no existe, no pasa nada
    
    private static final Logger logger = Logger.getLogger(CalculoCostoService.class.getName());


    public double calcularCostoEstimado(Ruta ruta) {
        // Validar que la ruta tiene tramos
        if (ruta == null || ruta.getTramos() == null || ruta.getTramos().isEmpty()) {
            throw new RuntimeException("No se puede calcular costo estimado para una ruta sin tramos");
        }

        Tarifa tarifa = tarifaService.getTarifaVigente();
        
        double distanciaTotal = ruta.getTramos().stream()
                .mapToDouble(Tramo::getDistanciaKm)
                .sum();

        double costoTrasladoEstimado = distanciaTotal * tarifa.getCostoBasePorKm();

        double costoCombustibleEstimado = distanciaTotal
                * tarifa.getConsumoPromedioCombustible()
                * tarifa.getCostoLitroCombustible();

        // Estadia es estimada, ejemplo genérico = 1 día por depósito atravesado
        int cantidadDepositos = ruta.getTramos().size() - 1;
        double costoEstadiaEstimado = cantidadDepositos * tarifa.getCostoEstadiaDiariaDeposito();

        return costoTrasladoEstimado + costoCombustibleEstimado + costoEstadiaEstimado;

    }

    /**
     * CALCULO REAL (cuando TODOS los tramos tienen camión y fechas)
     * Con manejo de errores: si falla la obtención de datos de microservicios,
     * usa valores fallback (costo estimado del tramo).
     */
    public Double calcularCostoReal(Solicitud solicitud) {

        Ruta ruta = solicitud.getRutaAsignada();

        Tarifa tarifa = tarifaService.getTarifaVigente();

        double costoTotal = 0.0;

        for (int i = 0; i < ruta.getTramos().size(); i++) {

            Tramo actual = ruta.getTramos().get(i);
            double costoTramo = calcularCostoRealTramo(actual, tarifa, i, ruta);
            costoTotal += costoTramo;
        }

        return costoTotal;
    }

    /**
     * Calcula el costo real de un tramo individual con manejo de errores.
     * Si falla la obtención de datos de microservicios, usa valores fallback.
     */
    private double calcularCostoRealTramo(Tramo actual, Tarifa tarifa, int index, Ruta ruta) {
        try {
            CamionDTO camion = null;
            DepositoDTO deposito = null;
            
            // Intentar obtener datos del camión con manejo de error
            try {
                camion = camionClient.obtenerCamionPorId(actual.getPatenteCamion());
            } catch (Exception e) {
                logger.warning("No se pudo obtener camión ID: " + actual.getPatenteCamion() + ". Usando fallback. Error: " + e.getMessage());
                camion = null;
            }

            double costoTraslado = 0.0;
            double costoCombustible = 0.0;

            if (camion != null) {
                // Usar datos reales del camión
                costoTraslado = actual.getDistanciaKm() * camion.getCostoBasePorKm().doubleValue();
                costoCombustible = actual.getDistanciaKm() * camion.getConsumoPorKm().doubleValue()
                        * tarifa.getCostoLitroCombustible();
            } else {
                // Fallback: usar costo aproximado del tramo (ya tenemos esto guardado)
                costoTraslado = actual.getCostoAproximado() != null ? actual.getCostoAproximado() : 0.0;
                logger.info("Usando fallback para costos del tramo " + actual.getId() + ": " + costoTraslado);
            }

            double costoEstadia = 0.0;

            // Si existe tramo siguiente, calculamos estadía
            if (index < ruta.getTramos().size() - 1) {
                Tramo siguiente = ruta.getTramos().get(index + 1);
                
                // Intentar obtener datos del depósito con manejo de error
                try {
                    deposito = depositoClient.obtenerDepositoPorId(actual.getIdUbicacionDestino());
                    
                    if (actual.getFechaFin() != null && siguiente.getFechaInicio() != null) {
                        long diasEstadia = ChronoUnit.DAYS.between(actual.getFechaFin(), siguiente.getFechaInicio());
                        costoEstadia = diasEstadia * deposito.getCostoEstadiaDiaria();
                    }
                } catch (Exception e) {
                    logger.warning("No se pudo obtener depósito ID: " + actual.getIdUbicacionDestino() + 
                            ". No se calcula estadía. Error: " + e.getMessage());
                    costoEstadia = 0.0;
                }
            }

            return costoTraslado + costoCombustible + costoEstadia + tarifa.getCostoFijoPorTramo();
            
        } catch (Exception e) {
            logger.severe("Error calculando costo real del tramo " + actual.getId() + ": " + e.getMessage());
            // Como último fallback, retornar el costo aproximado del tramo
            return actual.getCostoAproximado() != null ? actual.getCostoAproximado() : 0.0;
        }
    }
}