package com.microservicios.Solicitudes.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.client.TransporteServiceClient;
import com.microservicios.Solicitudes.dto.responses.TramoDTO;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.TipoTramo;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.TramoRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class TramoService {
    private static final Logger logger = LoggerFactory.getLogger(TramoService.class);

    private final TramoRepository tramoRepository;
    private final SolicitudService solicitudService;
    private final CalculoCostoService calculoCostoService;
    private final TransporteServiceClient transporteServiceClient;

    public TramoDTO asignarCamion(Integer idTramo, String patenteCamion) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        Integer idContenedor = tramo.getRuta().getSolicitud().getIdContenedor();

        if (transporteServiceClient.verificarCapacidad(patenteCamion, idContenedor)) {
            transporteServiceClient.ocuparCamion(patenteCamion);
            tramo.setPatenteCamion(patenteCamion);

            Tramo tramoGuardado = tramoRepository.save(tramo);
                        // --- ¡NUEVO: Log de Asignación de Camión! ---
            logger.info("CAMION ASIGNADO: Patente [{}] asignada a Tramo ID [{}] (Ruta ID [{}])",
                    patenteCamion,
                    idTramo,
                    tramo.getRuta().getId());
            TramoDTO tramodto = convertTramoDTO(tramoGuardado, "se registro el camion con patente: " + patenteCamion);
        
            return tramodto;
        }
        throw new RuntimeException("El camión no es adecuado para el contenedor del tramo");
    }

    public TramoDTO iniciarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        if (tramo.getPatenteCamion() == null) {
            throw new RuntimeException("No se puede iniciar un tramo sin camión asignado");
        }

        solicitudService.cambiarEstadoSolicitud(tramo.getRuta().getSolicitud(), "EN_RUTA", "EN_CAMINO");

        // Guardar el tramo actualizado

        tramo.setFechaInicio(LocalDateTime.now());

        // Intentar calcular el costo real del tramo anterios ya que ahora tiene los
        // dias de estadia
        TipoTramo tipoTramo = tramo.getTipoTramo();
        try {
            if (tipoTramo.equals(TipoTramo.DEPOSITO_DEPOSITO) || tipoTramo.equals(TipoTramo.DEPOSITO_DESTINO)) {
                Tramo tramoAnterior = tramoRepository.findById(tramo.getId() - 1)
                        .orElseThrow(() -> new RuntimeException("Tramo anterior no encontrado"));
                calculoCostoService.calcularCostoRealTramo(tramoAnterior, tramo);
                tramoRepository.save(tramoAnterior);

            } else {
                if (tipoTramo.equals(TipoTramo.ORIGEN_DESTINO)) {
                    calculoCostoService.calcularCostoRealTramo(tramo, null);
                    // tramoRepository.save(tramo);
                }
            }

        } catch (Exception e) {
            // Si falla el cálculo, loguear pero NO fallar
            logger.error("Error calculando costo real del tramo {}: {}", idTramo, e.getMessage()); // ⬅️ Corregido
            // Usar el costo aproximado como fallback
            tramo.setCostoReal(0.0);
        }
    
        Tramo guardado = tramoRepository.save(tramo);
        TramoDTO dto = convertTramoDTO(guardado, "se registro le fecha hora inicio del tramo");
        return dto;
    }

    public TramoDTO finalizarTramo(Integer idTramo) {
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
        tramo.setFechaFin(LocalDateTime.now());

        TipoTramo tipoTramo = tramo.getTipoTramo();

        if (tipoTramo.equals(TipoTramo.DEPOSITO_DESTINO) || tipoTramo.equals(TipoTramo.ORIGEN_DESTINO)) {
            try {
                calculoCostoService.calcularCostoRealTramo(tramo, null);
                solicitudService.finalizarSolicitud(tramo.getRuta().getSolicitud());
            } catch (Exception e) {
                // Fallback si falla el cálculo (ej: camión/tarifa no encontrada)
                logger.error("Error calculando costo real del tramo final {}: {}", idTramo, e.getMessage()); // ⬅️ Corregido
                tramo.setCostoReal(0.0);
            }

        } else if (tipoTramo.equals(TipoTramo.ORIGEN_DEPOSITO) || tipoTramo.equals(TipoTramo.DEPOSITO_DEPOSITO)) {
            solicitudService.cambiarEstadoSolicitud(tramo.getRuta().getSolicitud(), "EN_DEPOSITO", "EN_DEPOSITO");

        }
        transporteServiceClient.liberarCamion(tramo.getPatenteCamion());
        Tramo guardado = tramoRepository.save(tramo);
        TramoDTO dto = convertTramoDTO(guardado, "se registro le fecha hora fin del tramo");
         logger.info("TRAMO FINALIZADO: Tramo ID [{}] (Ruta ID [{}])", 
            tramo.getId(),
            tramo.getRuta().getId()
        );

        // --- ¡NUEVO: Lógica de Fin de Solicitud! ---
        verificarSiSolicitudTermino(tramo);
        return dto;
    }

     private void verificarSiSolicitudTermino(Tramo tramoFinalizado) {
        Ruta ruta = tramoFinalizado.getRuta();

        // 1. Obtener todos los tramos de esta ruta
        List<Tramo> tramos = tramoRepository.findByRuta(ruta);

        // 2. Verificar si TODOS los tramos tienen fecha de fin
        boolean todosFinalizados = tramos.stream()
                                    .allMatch(t -> t.getFechaFin() != null);

        if (todosFinalizados) {
            // ¡Es el último tramo!
            Solicitud solicitud = ruta.getSolicitud();
            solicitudService.cambiarEstadoSolicitud(solicitud,"FINALIZADA", "ENTREGADO"); // O el estado final
            solicitudService.actualizarSolicitud(solicitud);

            // --- ¡NUEVO: Log de Fin de Solicitud! ---
            logger.info("SOLICITUD COMPLETADA: Todos los tramos de la Ruta ID [{}] están finalizados. Solicitud ID [{}] marcada como ENTREGADA.",
                ruta.getId(),
                solicitud.getId()
            );
        }
    }

    public void eliminarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        tramoRepository.delete(tramo);
    }

    private TramoDTO convertTramoDTO (Tramo tramo, String msj) {
        TramoDTO dto = TramoDTO.builder()
            .id(tramo.getId())
            .patenteCamion(tramo.getPatenteCamion())
            .tipoTramo(tramo.getTipoTramo().name())
            .fechaFin(tramo.getFechaFin())
            .fechaInicio(tramo.getFechaInicio())
            .mensaje(msj)
            .build();
        return dto;
    }
}
