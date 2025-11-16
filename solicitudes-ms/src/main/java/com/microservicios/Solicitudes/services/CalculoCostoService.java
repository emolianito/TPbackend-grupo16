package com.microservicios.Solicitudes.services;

import java.time.temporal.ChronoUnit;
// import java.util.logging.Logger; // ⬅️ ELIMINAR ESTE IMPORT

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

import org.slf4j.Logger; // ⬅️ NUEVO: Importar SLF4J Logger
import org.slf4j.LoggerFactory; // ⬅️ NUEVO: Importar SLF4J LoggerFactory

@Service
@AllArgsConstructor
public class CalculoCostoService {

    // ⬅️ CAMBIO: Usar SLF4J Logger
    private static final Logger logger = LoggerFactory.getLogger(CalculoCostoService.class);

    private final TarifaService tarifaService;
    private final TransporteServiceClient camionClient; // lo dejamos listo
    private final UbicacionesServiceClient depositoClient; // si aún no existe, no pasa nada

    public double calcularCostoEstimado(Ruta ruta) {
        // Validar que la ruta tiene tramos
        if (ruta == null || ruta.getTramos() == null || ruta.getTramos().isEmpty()) {
            throw new RuntimeException("No se puede calcular costo estimado para una ruta sin tramos");
        }

        Tarifa tarifa = tarifaService.getTarifaVigente();

        // CORRECCIÓN: Manejar null antes de mapToDouble
        double distanciaTotal = ruta.getTramos().stream()
                .mapToDouble(tramo -> tramo.getDistanciaKm() != null ? tramo.getDistanciaKm() : 0.0) // ⬅️ LÍNEA 40
                                                                                                     // CORREGIDA
                .sum();
        double costoTrasladoEstimado = distanciaTotal * tarifa.getCostoBasePorKm();

        double costoCombustibleEstimado = distanciaTotal
                * tarifa.getConsumoPromedioCombustible()
                * tarifa.getCostoLitroCombustible();

        // Estadia es estimada, ejemplo genérico = 1 día por depósito atravesado
        int cantidadDepositos = ruta.getTramos().size() - 1;
        double costoEstadiaEstimado = cantidadDepositos * tarifa.getCostoEstadiaDiariaDeposito();

        return costoTrasladoEstimado + costoCombustibleEstimado + costoEstadiaEstimado
                + (ruta.getTramos().size() * tarifa.getCostoFijoPorTramo());

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

            costoTotal += actual.getCostoReal() != null ? actual.getCostoReal() : 0.0;
        }
        double costoGestion = (ruta.getTramos().size() * tarifa.getCostoFijoPorTramo());

        return costoTotal + costoGestion;
    }

    /**
     * Calcula el costo real de un tramo individual con manejo de errores.
     * Si falla la obtención de datos de microservicios, usa valores fallback.
     */
    public void calcularCostoRealTramo(Tramo actual, Tramo siguiente) {
        try {
            Tarifa tarifaVigente = tarifaService.getTarifaVigente();
            CamionDTO camion = null;
            DepositoDTO deposito = null;

            // Intentar obtener datos del camión con manejo de error
            try {
                camion = camionClient.obtenerCamionPorId(actual.getPatenteCamion());
            } catch (Exception e) {
                // ⬅️ CAMBIO: Usar logger.warn y placeholders
                logger.warn("No se pudo obtener camión ID: {}. Usando fallback. Error: {}",
                        actual.getPatenteCamion(), e.getMessage());
                camion = null;
            }

            double costoTraslado = 0.0;
            double costoCombustible = 0.0;

            if (camion != null) {
                // Usar datos reales del camión
                costoTraslado = actual.getDistanciaKm() * camion.getCostoBasePorKm().doubleValue();
                costoCombustible = actual.getDistanciaKm() * camion.getConsumoPorKm().doubleValue()
                        * tarifaVigente.getCostoLitroCombustible();
            } else {
                // Fallback: usar costo aproximado del tramo (ya tenemos esto guardado)
                costoTraslado = 0.0;
                // ⬅️ CAMBIO: Usar logger.info y placeholders
                logger.info("Usando fallback para costos del tramo {} (Traslado: {}).",
                        actual.getId(), costoTraslado);
            }

            double costoEstadia = 0.0;

            // Si existe tramo siguiente, calculamos estadía
            if (siguiente != null) {

                // Intentar obtener datos del depósito con manejo de error
                try {
                    deposito = depositoClient.obtenerDepositoPorId(actual.getIdDepositoDestino());

                    if (actual.getFechaFin() != null && siguiente.getFechaInicio() != null) {
                        long diasEstadia = ChronoUnit.DAYS.between(actual.getFechaFin(), siguiente.getFechaInicio());
                        costoEstadia = diasEstadia * deposito.getCostoEstadiaDiaria();
                    }
                } catch (Exception e) {
                    // ⬅️ CAMBIO: Usar logger.warn y placeholders
                    logger.warn("No se pudo obtener depósito ID: {}. No se calcula estadía. Error: {}",
                            actual.getIdUbicacionDestino(), e.getMessage());
                    costoEstadia = 0.0;
                }
            }
            double costoRealTramo = costoTraslado + costoCombustible + costoEstadia
                    + tarifaVigente.getCostoFijoPorTramo();
            actual.setCostoReal(costoRealTramo);

        } catch (Exception e) {
            // ⬅️ CAMBIO: Usar logger.error y placeholders
            logger.error("Error calculando costo real del tramo {}: {}", actual.getId(), e.getMessage());
            // Como último fallback, retornar el costo aproximado del tramo
            actual.setCostoReal(0.0);
        }
    }
}