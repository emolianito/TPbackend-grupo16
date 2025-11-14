package com.microservicios.Solicitudes.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.repository.SolicitudRepository;

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
        solicitud.setEstado(com.microservicios.Solicitudes.entity.EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDate.now());

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

    public Solicitud finalizarSolicitud(Solicitud solicitud) {
        solicitud.setEstado(com.microservicios.Solicitudes.entity.EstadoSolicitud.FINALIZADA);
        //deben ser calculados
        solicitud.setCostoReal(222.0); 
        solicitud.setTiempoReal("2 horas");

        return solicitudRepository.save(solicitud);
    }

    /**
     * Elimina una solicitud por ID
     */
    public void eliminarSolicitud(Integer id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitudRepository.delete(solicitud);
    }

    public SolicitudDTO finalizarSolicitud(Integer idSolicitud) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                
        Ruta ruta = solicitud.getRutaAsignada();
        Double costoRealTotal = ruta.getTramos().stream()
                    .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                    .sum();

            // IMPORTANTE: Actualizar la solicitud con estado FINALIZADA
        solicitud.setEstado(com.microservicios.Solicitudes.entity.EstadoSolicitud.FINALIZADA);
        //deben ser calculados
        solicitud.setCostoReal(costoRealTotal);
        //debera calcular tempo real
        solicitud.setTiempoReal("2 horas");

        Solicitud solicitudFinalizada = solicitudRepository.save(solicitud);
        return convertToDTO(solicitudFinalizada);
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
}