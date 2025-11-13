package com.microservicios.Solicitudes.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.client.TransporteServiceClient;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.TramoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository tramoRepository;
    private final SolicitudService solicitudService;
    private final CalculoCostoService calculoCostoService;
    private final TransporteServiceClient transporteServiceClient;

    public void asignarCamion(Integer idTramo, String patenteCamion) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        
        Integer idContenedor = tramo.getRuta().getSolicitud().getIdContenedor();
        
        if (transporteServiceClient.verificarCapacidad(patenteCamion, idContenedor)) {
            transporteServiceClient.ocuparCamion(patenteCamion);
            tramo.setPatenteCamion(patenteCamion);
            tramoRepository.save(tramo);
            return;
        }
        throw new RuntimeException("El camión no es adecuado para el contenedor del tramo");
    }

    public void iniciarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        tramo.setFechaInicio(LocalDate.now());
        tramoRepository.save(tramo);
    }


    public Tramo finalizarTramo(Integer idTramo) {
        // Obtener el tramo y validar que existe
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        // Validar que el tramo no esté ya finalizado
        if (tramo.getFechaFin() != null) {
            throw new RuntimeException("El tramo ya está finalizado");
        }

        // Validar que el tramo haya sido iniciado
        if (tramo.getFechaInicio() == null) {
            throw new RuntimeException("No se puede finalizar un tramo que no ha sido iniciado");
        }

        // Validar que tenga un camión asignado
        if (tramo.getPatenteCamion() == null) {
            throw new RuntimeException("No se puede finalizar un tramo sin camión asignado");
        }

        // Establecer la fecha de finalización
        tramo.setFechaFin(LocalDate.now());
        
        // Intentar calcular el costo real del tramo
        try {
            Double costoReal = calculoCostoService.calcularCostoReal(tramo.getRuta().getSolicitud());
            tramo.setCostoReal(costoReal);
        } catch (Exception e) {
            // Si falla el cálculo, loguear pero NO fallar
            System.err.println("Error calculando costo real del tramo " + idTramo + ": " + e.getMessage());
            // Usar el costo aproximado como fallback
            tramo.setCostoReal(tramo.getCostoAproximado());
        }
        
        // Guardar el tramo actualizado
        tramoRepository.save(tramo);

        // Obtener la ruta asociada
        Ruta ruta = tramo.getRuta();
        Solicitud solicitud = ruta.getSolicitud();
        
        // Verificar si todos los tramos de la ruta están finalizados
        boolean todosLosTramosFianlizados = ruta.getTramos().stream()
                .allMatch(t -> t.getFechaFin() != null);

        if (todosLosTramosFianlizados) {
            solicitudService.finalizarSolicitud(solicitud.getId());
        }

         transporteServiceClient.liberarCamion(tramo.getPatenteCamion());
            

        return tramo;
    }

    /**
     * Elimina un tramo por ID
     */
    public void eliminarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        tramoRepository.delete(tramo);
    }
}


