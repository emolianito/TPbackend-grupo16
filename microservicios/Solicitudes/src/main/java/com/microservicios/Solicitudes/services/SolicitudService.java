package com.microservicios.Solicitudes.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.entity.CambioEstadoSolicitud;
import com.microservicios.Solicitudes.entity.EstadoSolicitud;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.repository.SolicitudRepository;

import static com.microservicios.Solicitudes.entity.EstadoSolicitud.*;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }
    
    public Solicitud createSolicitud(CrearSolicitudDTO dto) {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdCliente(dto.getIdCliente());
        solicitud.setIdContenedor(dto.getIdContenedor());
        solicitud.setFechaSolicitud(LocalDate.now());
        cambiarEstadoSolicitud(solicitud, SOLICITADA, "EN_ORIGEN");

        return solicitudRepository.save(solicitud);
    }

    public SolicitudDTO getSolicitudById(int id) {
        Solicitud solicitud = solicitudRepository.findById(id).get();
        return convertToDTO(solicitud);
    }

    public List<SolicitudDTO> getAllSolicitudes() {
        List<Solicitud> solicitudes = solicitudRepository.findAll();
        return solicitudes.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /* 
    public Solicitud finalizarSolicitud(Solicitud solicitud) {
        solicitud.setEstado(com.microservicios.Solicitudes.entity.EstadoSolicitud.FINALIZADA);
        //deben ser calculados
        solicitud.setCostoReal(222.0); 
        solicitud.setTiempoReal("2 horas");

        return solicitudRepository.save(solicitud);
    }
    */

    /**
     * Elimina una solicitud por ID
     */
    public void eliminarSolicitud(Integer id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitudRepository.delete(solicitud);
    }

    //PODRIA PASAR DIRECTAMENTE LA ENTIDAD SOLICITUD
    public SolicitudDTO finalizarSolicitud(Integer idSolicitud) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                
        Ruta ruta = solicitud.getRutaAsignada();
        Double costoRealTotal = ruta.getTramos().stream()
                    .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                    .sum();

            // IMPORTANTE: Actualizar la solicitud con estado FINALIZADA
        cambiarEstadoSolicitud(solicitud,FINALIZADA, "ENTREGADO");
        //deben ser calculados
        solicitud.setCostoReal(costoRealTotal);

        //:TODO debera calcular tempo real
        solicitud.setTiempoReal("2 horas");

        Solicitud solicitudFinalizada = solicitudRepository.save(solicitud);
        return convertToDTO(solicitudFinalizada);
    }

    public Solicitud actualizarSolicitud(Solicitud solicitud) {
        Solicitud solicitudExistente = solicitudRepository.findById(solicitud.getId())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return solicitudRepository.save(solicitudExistente);
    }

    public Solicitud getSolicitudEntityById(int id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }
    
    private SolicitudDTO convertToDTO(Solicitud solicitud) {
        SolicitudDTO dto = new SolicitudDTO();
        dto.setId(solicitud.getId());
        dto.setIdContenedor(solicitud.getIdContenedor());
        dto.setIdCliente(solicitud.getIdCliente());
        dto.setCostoEstimado(solicitud.getCostoEstimado());
        dto.setCostoReal(solicitud.getCostoReal());
        dto.setTiempoEstimado(solicitud.getTiempoEstimado());
        dto.setTiempoReal(solicitud.getTiempoReal());
        dto.setRutaAsignada(solicitud.getRutaAsignada());
        dto.setEstado(solicitud.getEstado());
        dto.setFechaSolicitud(solicitud.getFechaSolicitud());
        // Las rutasSugeridas NO se incluyen en el DTO
        return dto;
    }

    public void cambiarEstadoSolicitud(Solicitud solicitud, EstadoSolicitud nuevoEstado, String estadoContenedor) {
        List<CambioEstadoSolicitud> cambiosEstado = solicitud.getCambiosEstado();
        if (!cambiosEstado.isEmpty()) {
            for (CambioEstadoSolicitud cambio : cambiosEstado) {
                if (cambio.getFechaHoraFin() == null) {
                    cambio.setFechaHoraFin(LocalDate.now().atStartOfDay());
                }
            }
        }

        CambioEstadoSolicitud cambio = new CambioEstadoSolicitud(LocalDate.now().atStartOfDay(), nuevoEstado, estadoContenedor);
        solicitud.addCambioEstado(cambio);
        solicitud.setEstado(nuevoEstado);
        solicitudRepository.save(solicitud);
    }   


}