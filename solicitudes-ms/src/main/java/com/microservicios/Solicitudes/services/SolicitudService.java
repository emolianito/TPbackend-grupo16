package com.microservicios.Solicitudes.services;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator; 
import java.time.Duration; 

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.dto.responses.EstadoContenedorDTO;
import com.microservicios.Solicitudes.client.ClienteServiceClient;
import com.microservicios.Solicitudes.dto.external.ContenedorDTO;
import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.CambioEstadoSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.entity.CambioEstadoSolicitud;
import com.microservicios.Solicitudes.entity.EstadoSolicitud;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.EstadoSolicitudRepository;
import com.microservicios.Solicitudes.repository.SolicitudRepository;

import lombok.AllArgsConstructor;

// ¡Añadir imports de Logger!
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class SolicitudService {
    private static final Logger logger = LoggerFactory.getLogger(SolicitudService.class);
    private final SolicitudRepository solicitudRepository;
    private final ClienteServiceClient clienteServiceClient;
    private final EstadoSolicitudRepository estadoSolicitudRepository;

    public Solicitud createSolicitud(CrearSolicitudDTO dto) {
        Solicitud solicitud = new Solicitud();
        solicitud.setDniCliente(dto.getDniCliente());
        solicitud.setIdContenedor(dto.getIdContenedor());
        solicitud.setFechaSolicitud(LocalDate.now());
        cambiarEstadoSolicitud(solicitud, "SOLICITADA", "EN_ORIGEN");

         // --- ¡NUEVO: Log de Creación! ---
        // Usamos {} como placeholders para los IDs. Es más eficiente.
        logger.info("SOLICITUD CREADA: ID [{}], Cliente DNI [{}], Contenedor ID [{}]", 
            solicitud.getId(), 
            solicitud.getDniCliente(), // Idealmente usar un ID de cliente, no DNI
            solicitud.getIdContenedor()
        );
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
    public SolicitudDTO finalizarSolicitud(Solicitud solicitud) {

        Ruta ruta = solicitud.getRutaAsignada();

        // ⬅️ VALIDACIÓN: Asegurar que la ruta y los tramos existan
        if (ruta == null || ruta.getTramos() == null || ruta.getTramos().isEmpty()) {
            throw new RuntimeException("No se puede finalizar la solicitud: no tiene ruta asignada o tramos.");
        }

        // 1. Encontrar la fecha de inicio REAL más temprana (inicio del primer tramo)
        LocalDateTime fechaInicioReal = ruta.getTramos().stream()
                .map(Tramo::getFechaInicio) // Mapear a la fecha de inicio de cada tramo
                .filter(java.util.Objects::nonNull) // Ignorar tramos sin inicio (si los hubiese)
                .min(Comparator.naturalOrder()) // Encontrar la fecha más antigua (inicio de la ruta)
                .orElseThrow(() -> new RuntimeException(
                        "No se puede calcular el tiempo real: el primer tramo no ha sido iniciado."));

        // 2. Encontrar la fecha de fin REAL más tardía (fin del último tramo)
        LocalDateTime fechaFinReal = ruta.getTramos().stream()
                .map(Tramo::getFechaFin) // Mapear a la fecha de fin de cada tramo
                .filter(java.util.Objects::nonNull) // Ignorar tramos sin fin
                .max(Comparator.naturalOrder()) // Encontrar la fecha más reciente (fin de la ruta)
                .orElseThrow(() -> new RuntimeException(
                        "No se puede calcular el tiempo real: el último tramo no ha sido finalizado."));

        // 3. Calcular la duración total REAL
        Duration duracionReal = Duration.between(fechaInicioReal, fechaFinReal);

        // 4. Formatear la duración
        String tiempoRealFormateado = formatDuration(duracionReal);

        Double costoRealTotal = ruta.getTramos().stream()
                .mapToDouble(t -> t.getCostoReal() != null ? t.getCostoReal() : 0.0)
                .sum();

        // IMPORTANTE: Actualizar la solicitud con estado FINALIZADA
        cambiarEstadoSolicitud(solicitud, "FINALIZADA", "ENTREGADO");
        // deben ser calculados
        solicitud.setCostoReal(costoRealTotal);

        // :TODO debera calcular tempo real
        solicitud.setTiempoReal(tiempoRealFormateado);

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
        dto.setDniCliente(solicitud.getDniCliente());
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
                    cambio.setFechaHoraFin(LocalDateTime.now());
                }
            }
        }
        

        CambioEstadoSolicitud cambio = new CambioEstadoSolicitud(LocalDateTime.now(), nuevoEstado,
                estadoContenedor, solicitud);

        if (nuevoEstado.equals("FINALIZADA")){
            cambio.setFechaHoraFin(LocalDateTime.now());
        }
        
        solicitud.addCambioEstado(cambio);

        try {
            ContenedorDTO contenedorDTO = clienteServiceClient.obtenerContenedorPorId(solicitud.getIdContenedor());
            // TODO: DEFINIR BIEN LA IGUALDAD DE LOS ESTADOS

            List<EstadoContenedorDTO> estadosContenedorDTO = clienteServiceClient.obtenerEstadosContendor();
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

        // ⬅️ Lógica simplificada: Obtener, ordenar y mapear en un solo paso.
        return solicitud.getCambiosEstado().stream()
                // 1. Ordenar por la fechaHoraInicio del más antiguo al más nuevo
                .sorted(Comparator.comparing(CambioEstadoSolicitud::getFechaHoraInicio))
                // 2. Mapear la entidad al DTO
                .map(cambio -> CambioEstadoSolicitudDTO.builder()
                        .fechaHoraInicio(cambio.getFechaHoraInicio())
                        .fechaHoraFin(cambio.getFechaHoraFin())
                        .estadoSolicitud(cambio.getEstadoSolicitud())
                        .estadoContenedor(cambio.getEstadoContenedor())
                        .build())
                // 3. Recolectar los resultados en una lista
                .toList(); // Usa .collect(Collectors.toList()) si no estás en Java 16+
    }
    // Dentro de la clase SolicitudService

    /**
     * Convierte un objeto Duration al formato String D:HH:MM.
     * Este método se basa en la lógica de formato de RutaService.asignarRuta.
     */
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        // Formato D:HH:MM
        return String.format("%d:%02d:%02d", days, hours, minutes);
    }

}