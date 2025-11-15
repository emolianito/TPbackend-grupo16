package com.microservicios.Solicitudes.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservicio.Solicitudes.dto.responses.EstadoContenedorDTO;
import com.microservicios.Solicitudes.client.ClienteServiceClient;
import com.microservicios.Solicitudes.dto.external.ContenedorDTO;
import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.CambioEstadoSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.entity.CambioEstadoSolicitud;
import com.microservicios.Solicitudes.entity.EstadoSolicitud;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.repository.EstadoSolicitudRepository;
import com.microservicios.Solicitudes.repository.SolicitudRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final ClienteServiceClient clienteServiceClient;
    private final EstadoSolicitudRepository estadoSolicitudRepository;

    public Solicitud createSolicitud(CrearSolicitudDTO dto) {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdCliente(dto.getIdCliente());
        solicitud.setIdContenedor(dto.getIdContenedor());
        solicitud.setFechaSolicitud(LocalDate.now());
        cambiarEstadoSolicitud(solicitud, "SOLICITADA", "EN_ORIGEN");

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
     * public Solicitud finalizarSolicitud(Solicitud solicitud) {
     * solicitud.setEstado(com.microservicios.Solicitudes.entity.EstadoSolicitud.
     * FINALIZADA);
     * //deben ser calculados
     * solicitud.setCostoReal(222.0);
     * solicitud.setTiempoReal("2 horas");
     * 
     * return solicitudRepository.save(solicitud);
     * }
     */

    /**
     * Elimina una solicitud por ID
     */
    public void eliminarSolicitud(Integer id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitudRepository.delete(solicitud);
    }

    // PODRIA PASAR DIRECTAMENTE LA ENTIDAD SOLICITUD
    public SolicitudDTO finalizarSolicitud(Integer idSolicitud) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Ruta ruta = solicitud.getRutaAsignada();
        Double costoRealTotal = ruta.getTramos().stream()
                .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                .sum();

        // IMPORTANTE: Actualizar la solicitud con estado FINALIZADA
        cambiarEstadoSolicitud(solicitud, "FINALIZADA", "ENTREGADO");
        // deben ser calculados
        solicitud.setCostoReal(costoRealTotal);

        // :TODO debera calcular tempo real
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

    public void cambiarEstadoSolicitud(Solicitud solicitud, String nuevoEstado, String estadoContenedor) {
        EstadoSolicitud estadoSolicitud = estadoSolicitudRepository.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado de solicitud no encontrado: " + nuevoEstado));

        List<CambioEstadoSolicitud> cambiosEstado = solicitud.getCambiosEstado();
        if (cambiosEstado != null) {
            for (CambioEstadoSolicitud cambio : cambiosEstado) {
                if (cambio.getFechaHoraFin() == null) {
                    cambio.setFechaHoraFin(LocalDate.now().atStartOfDay());
                }
            }
        }

        CambioEstadoSolicitud cambio = new CambioEstadoSolicitud(LocalDate.now().atStartOfDay(), nuevoEstado,
                estadoContenedor);
        solicitud.addCambioEstado(cambio);

        try {
            ContenedorDTO contenedorDTO = clienteServiceClient.obtenerContenedorPorId(solicitud.getIdContenedor());
            // TODO: DEFINIR BIEN LA IGUALDAD DE LOS ESTADOS

            List<EstadoContenedorDTO> estadosContendorDTO = clienteServiceClient.obtenerEstadosContendor();
            Integer idEstado = estadosContenedorDTO.stream()
                    // 1. Filtra la lista por el nombre que coincide
                    .filter(estado -> estado.getNombre().equals(estadoContenedor))
                    // 2. Mapea al ID
                    .map(estado -> estado.getId())
                    // 3. Obtiene el resultado, o lanza una excepción si no lo encuentra.
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("El nombre de estado de contenedor '" + estadoContenedor
                            + "' no fue encontrado en el servicio de Clientes."));
            contenedorDTO.setEstadoId(idEstado); // POR AHORA SE SETEA EN 1 (EN ORIGEN)

            clienteServiceClient.actualizarContenedor(contenedorDTO);
        } catch (RuntimeException e) {
            // Captura el error de 'orElseThrow' o cualquier error de negocio.
            System.err.println("Error de negocio: " + e.getMessage());
            // Aquí puedes añadir lógica para deshacer el cambio de estado de la solicitud
            throw e; // Relanza la excepción para que el endpoint HTTP falle.
        } catch (Exception e) {
            // Captura errores de comunicación del RestClient
            System.err.println("Error CRÍTICO al comunicarse con ClienteService: " + e.getMessage());
            // Aquí puedes añadir lógica para deshacer el cambio de estado de la solicitud
            throw new RuntimeException("Fallo la comunicación con el servicio de Clientes.", e);
        }

        solicitud.setEstado(estadoSolicitud);
        solicitudRepository.save(solicitud);
    }

    public List<CambioEstadoSolicitudDTO> getCambiosEstadoSolicitud(int solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        List<CambioEstadoSolicitudDTO> cambiosEstadoDTO = new ArrayList<>();
        List<CambioEstadoSolicitud> cambiosEstado = solicitud.getCambiosEstado();
        for (CambioEstadoSolicitud cambio : cambiosEstado) {
            CambioEstadoSolicitudDTO dto = CambioEstadoSolicitudDTO.builder()
                    .fechaHoraInicio(cambio.getFechaHoraInicio())
                    .fechaHoraFin(cambio.getFechaHoraFin())
                    .estadoSolicitud(cambio.getEstadoSolicitud())
                    .estadoContenedor(cambio.getEstadoContenedor())
                    .build();
            cambiosEstadoDTO.add(dto);
        }

        return cambiosEstadoDTO;
    }

}