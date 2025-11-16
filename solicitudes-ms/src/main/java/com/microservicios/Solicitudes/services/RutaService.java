package com.microservicios.Solicitudes.services;

import com.microservicios.Solicitudes.client.UbicacionesServiceClient;
import com.microservicios.Solicitudes.dto.external.CoordenadasDTO;
import com.microservicios.Solicitudes.dto.external.DepositoDTO;
import com.microservicios.Solicitudes.dto.external.DistanciaDTO;
import com.microservicios.Solicitudes.dto.request.GenerarRutasRequestDTO;
import com.microservicios.Solicitudes.dto.responses.RutaTentativaDTO;
import com.microservicios.Solicitudes.dto.responses.TramoTentativoDTO;

import org.springframework.web.client.RestClientException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.TipoTramo;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.RutaRepository;
import com.microservicios.Solicitudes.repository.TramoRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class RutaService {
     private static final Logger logger = LoggerFactory.getLogger(RutaService.class);

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final SolicitudService solicitudService;
    private final CalculoCostoService calculoCostoService;

    private final UbicacionesServiceClient ubicacionesServiceClient;

    /**
     * Punto de entrada principal para generar rutas tentativas.
     * Guarda las rutas y sus tramos en la base de datos como entidades Ruta y
     * Tramo.
     *
     * @param request DTO con origenDireccion, destinoDireccion y cantidadDepositos
     * @return Una lista de DTOs de rutas sugeridas, ordenadas por km.
     */
    public List<RutaTentativaDTO> generarRutasTentativas(GenerarRutasRequestDTO request) {

        // --- ¡¡NUEVO PASO INICIAL: GEOCODIFICAR!! ---
        String origen;
        String destino;
        try {
            logger.info("Geocodificando origen: {}", request.getOrigenDireccion());
            CoordenadasDTO coordsOrigen = ubicacionesServiceClient.obtenerCoordenadas(request.getOrigenDireccion());

            logger.info("Geocodificando destino: {}", request.getDestinoDireccion());
            CoordenadasDTO coordsDestino = ubicacionesServiceClient.obtenerCoordenadas(request.getDestinoDireccion());

            // Convertimos las coordenadas a "lat,lon" para el resto de la lógica
            origen = coordsOrigen.getLatitud() + "," + coordsOrigen.getLongitud();
            destino = coordsDestino.getLatitud() + "," + coordsDestino.getLongitud();

            logger.info("Geocodificación OK: {} -> {}", origen, destino);

        } catch (RestClientException e) {
            logger.error("Error fatal geocodificando direcciones: {}", e.getMessage());
            // Si no podemos geocodificar, no podemos calcular.
            throw new RuntimeException("No se pudieron geocodificar las direcciones: " + e.getMessage());
        }
        // --- FIN DEL NUEVO PASO ---

        List<Ruta> rutasGuardadas = new ArrayList<>(); // Almacenaremos las entidades Ruta
        int k = request.getCantidadDepositos(); // El 'k' (número de paradas)

        // --- CASO 1: 0 Depósitos (Ruta Directa) ---
        if (k == 0) {
            logger.info("Calculando ruta directa (0 paradas)...");
            try {
                DistanciaDTO dist = ubicacionesServiceClient.obtenerDistancia(origen, destino);
                // Llamada al método que ahora guarda la entidad Ruta
                Ruta ruta = construirRutaDirecta(dist, request.getOrigenDireccion(),
                        request.getDestinoDireccion());
                rutasGuardadas.add(ruta);
            } catch (RestClientException e) {
                logger.error("Error al calcular ruta directa: {}", e.getMessage());
            }
        }

        // --- CASO 2 o más: 1+ Depósitos ---
        else {
            // 1. (Paso 3) Filtrar depósitos relevantes
            logger.info("Calculando rutas con {} parada(s)...", k);
            List<DepositoRelevante> depositosRelevantes = filtrarDepositosRelevantes(origen, destino);

            if (depositosRelevantes.isEmpty()) {
                logger.info("No se encontraron depósitos relevantes en la ruta.");
                return List.of(); // Lista vacía
            }

            // --- CASO 2.A: 1 Depósito (Optimización) ---
            if (k == 1) {
                logger.info("Calculando rutas con 1 parada (Optimizado)...");
                for (DepositoRelevante dep : depositosRelevantes) {
                    // Llamada al método que ahora guarda la entidad Ruta
                    Ruta ruta = construirRutaConUnaParada(dep, request.getOrigenDireccion(),
                            request.getDestinoDireccion());
                    rutasGuardadas.add(ruta);
                }
            }
            // --- CASO 2.B: 2+ Depósitos (Permutaciones) ---
            else if (k <= depositosRelevantes.size()) {
                logger.info("Calculando rutas con {} paradas (Permutaciones)...", k);

                List<List<DepositoRelevante>> permutaciones = generarPermutaciones(depositosRelevantes, k);

                for (List<DepositoRelevante> paradaOrdenada : permutaciones) {
                    try {
                        // Llamada al método que ahora guarda la entidad Ruta
                        Ruta ruta = construirRutaMultiParada(paradaOrdenada, request.getOrigenDireccion(),
                                request.getDestinoDireccion());
                        rutasGuardadas.add(ruta);
                    } catch (RestClientException e) {
                        logger.error("Error calculando ruta multi-parada: {}", e.getMessage());
                    }
                }
            } else {
                logger.info("Se pidieron {} paradas, pero solo hay {} depósitos relevantes.",
                        k, depositosRelevantes.size());
            }
        }

        // 4. Ordenar y devolver las mejores
        // Ordenamos las entidades por costo estimado (proxy para kilómetros)
        java.util.Collections.sort(rutasGuardadas, (r1, r2) -> {
            // La lambda sigue usando la función auxiliar, que ahora soporta días.
            int tiempo1Minutos = parseTiempoEstimado(r1.getTiempoEstimado());
            int tiempo2Minutos = parseTiempoEstimado(r2.getTiempoEstimado());

            // La comparación de enteros es siempre la forma más precisa de ordenar.
            return Integer.compare(tiempo1Minutos, tiempo2Minutos);
        });

        // Mapear a DTOs para el retorno
        List<RutaTentativaDTO> rutasSugeridas = rutasGuardadas.stream()
                .map(this::convertToRutaTentativaDTO)
                .toList();

        // 5. Devolver las mejores 10 rutas
        int maxResultados = Math.min(rutasSugeridas.size(), 10);
        return rutasSugeridas.subList(0, maxResultados);
    }

    // --- MÉTODOS AYUDANTES PARA CONSTRUIR Y PERSISTIR ENTIDADES ---

    /** Ayudante para persistir un tramo */
    private Tramo saveTramo(Ruta ruta, Integer idUbicacionOrigen, Integer idUbicacionDestino,
            TipoTramo tipoTramo, DistanciaDTO dist) {
        Tramo tramo = new Tramo();
        tramo.setRuta(ruta);
        // Usamos IDs de ubicación. 0 es un placeholder para Origen/Destino del cliente
        tramo.setIdUbicacionOrigen(idUbicacionOrigen);
        tramo.setIdUbicacionDestino(idUbicacionDestino);

        // ⬅️ CORRECCIÓN: Establecer idDepositoDestino solo si el tramo finaliza en un
        // depósito.
        if (tipoTramo == TipoTramo.ORIGEN_DEPOSITO || tipoTramo == TipoTramo.DEPOSITO_DEPOSITO) {
            // En estos casos, idUbicacionDestino es el ID del depósito.
            tramo.setIdDepositoDestino(idUbicacionDestino);
        }

        tramo.setTipoTramo(tipoTramo);
        // El campo 'tiempo' es el 'duracionTexto' de la API.
        tramo.setTiempo(dist.getDuracionTexto());
        // Se asegura de que la distancia en km se esté estableciendo antes de guardar.
        tramo.setDistanciaKm(dist.getKilometros());

        return tramoRepository.save(tramo);
    }

    /** Helper para persistir una ruta. */
    private Ruta saveRuta(List<Tramo> tramos, double kmTotales, String tiempoEstimado) {
        Ruta ruta = new Ruta();
        ruta.setSolicitud(null); // Sugerida (global)
        ruta.setTiempoEstimado(tiempoEstimado);
        ruta.setTramos(tramos);

        // 1. Guardar la ruta inicial para obtener el ID
        Ruta rutaGuardada = rutaRepository.save(ruta);

        // 2. Asociar la ruta a los tramos y actualizar los tramos
        for (Tramo tramo : tramos) {
            tramo.setRuta(rutaGuardada);
            tramoRepository.save(tramo);
        }

        // 3. Ya NO se guarda el costo estimado aquí.
        return rutaGuardada; // Devolver la ruta guardada.
    }

    /** Helper para sumar tiempos. */
    private String sumarTiempos(String t1, String t2) {
        if ((t1 == null || t1.isBlank()) && (t2 == null || t2.isBlank())) {
            return "00:00";
        }

        // Usar parseDuration para obtener objetos Duration y sumarlos
        java.time.Duration duration1 = parseDuration(t1);
        java.time.Duration duration2 = parseDuration(t2);

        java.time.Duration totalDuration = duration1.plus(duration2);

        // Formato de retorno HH:MM (solo tiempo de viaje)
        long hours = totalDuration.toHours();
        long minutes = totalDuration.toMinutes() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    // Métodos para convertir la entidad Ruta a DTO
    private RutaTentativaDTO convertToRutaTentativaDTO(Ruta ruta) {
        if (ruta == null || ruta.getTramos() == null) {
            return new RutaTentativaDTO(List.of(), 0.0);
        }

        List<TramoTentativoDTO> tramosDTO = ruta.getTramos().stream()
                .map(this::convertToTramoTentativoDTO)
                .toList();

        // El kilómetro total debe sumarse desde los tramos si no está en la Ruta.
        // Se asume que el costoAproximado en Tramo contiene los kilómetros
        double kmTotales = tramosDTO.stream()
                .mapToDouble(TramoTentativoDTO::getKilometros)
                .sum();

        return new RutaTentativaDTO(tramosDTO, kmTotales);
    }

    private TramoTentativoDTO convertToTramoTentativoDTO(Tramo tramo) {
        String origen = tramo.getIdUbicacionOrigen() == 0 ? "Origen Cliente"
                : "Depósito ID: " + tramo.getIdUbicacionOrigen();
        String destino = tramo.getIdUbicacionDestino() == 0 ? "Destino Cliente"
                : "Depósito ID: " + tramo.getIdUbicacionDestino();

        // El campo costoAproximado contiene los kilómetros del tramo (temporalmente).
        Double kilometros = tramo.getDistanciaKm() != null ? tramo.getDistanciaKm() : 0.0;

        String tipoTramoStr = tramo.getTipoTramo() != null ? tramo.getTipoTramo().name().replace('_', '-')
                : "DESCONOCIDO";

        return new TramoTentativoDTO(
                origen,
                destino,
                tipoTramoStr,
                kilometros,
                tramo.getTiempo());
    }

    /** Ayudante para el caso k=0 */
    private Ruta construirRutaDirecta(DistanciaDTO dist, String origen, String destino) {

        // 1. Crear y guardar el Tramo
        Tramo tramo = saveTramo(
                null, // Ruta aún no guardada
                0, // Origen cliente, ID 0 (Placeholder)
                0, // Destino cliente, ID 0 (Placeholder)
                TipoTramo.ORIGEN_DESTINO,
                dist);

        // 2. Crear y guardar la Ruta
        return saveRuta(List.of(tramo), dist.getKilometros(), dist.getDuracionTexto());
    }

    /** Ayudante para el caso k=1 (Usa la optimización del filtro) */
    private Ruta construirRutaConUnaParada(DepositoRelevante dep, String origen, String destino) {

        DistanciaDTO tramo1Dist = dep.tramoOrigenADeposito();
        DistanciaDTO tramo2Dist = dep.tramoDepositoADestino();

        Integer idDeposito = dep.deposito().getId();

        // 1. Crear y guardar el Tramo 1 (Origen -> Depósito)
        Tramo tramo1 = saveTramo(
                null,
                0, // Origen cliente, ID 0 (Placeholder)
                idDeposito, // Destino: ID del depósito
                TipoTramo.ORIGEN_DEPOSITO,
                tramo1Dist);

        // 2. Crear y guardar el Tramo 2 (Depósito -> Destino)
        Tramo tramo2 = saveTramo(
                null,
                idDeposito, // Origen: ID del depósito
                0, // Destino cliente, ID 0 (Placeholder)
                TipoTramo.DEPOSITO_DESTINO,
                tramo2Dist);

        // 3. Crear y guardar la Ruta
        double kmTotales = tramo1Dist.getKilometros() + tramo2Dist.getKilometros();
        String tiempoTotal = sumarTiempos(tramo1Dist.getDuracionTexto(), tramo2Dist.getDuracionTexto());
        // CORRECCIÓN: Crear una lista MUTABLE para los tramos
        java.util.List<Tramo> tramosList = new java.util.ArrayList<>();
        tramosList.add(tramo1);
        tramosList.add(tramo2);

        return saveRuta(tramosList, kmTotales, tiempoTotal);
    }

    /**
     * Ayudante para el caso k=2+ (Requiere llamadas a la API para tramos
     * intermedios)
     */
    private Ruta construirRutaMultiParada(List<DepositoRelevante> paradas,
            String origen, String destino) {

        List<Tramo> tramos = new ArrayList<>();
        double kmTotales = 0.0;
        String tiempoTotal = "00:00";

        // --- Tramo 1: Origen -> Parada 1 ---
        DepositoRelevante primeraParada = paradas.get(0);
        DistanciaDTO tramoOrigenDist = primeraParada.tramoOrigenADeposito();
        Integer idPrimeraParada = primeraParada.deposito().getId();

        Tramo tramoOrigen = saveTramo(
                null,
                0, // Origen cliente, ID 0 (Placeholder)
                idPrimeraParada,
                TipoTramo.ORIGEN_DEPOSITO,
                tramoOrigenDist);
        tramos.add(tramoOrigen);
        kmTotales += tramoOrigenDist.getKilometros();
        tiempoTotal = sumarTiempos(tiempoTotal, tramoOrigenDist.getDuracionTexto());

        // --- Tramos Intermedios: Parada 1 -> Parada 2 ... ---
        for (int i = 0; i < paradas.size() - 1; i++) {
            DepositoRelevante paradaActual = paradas.get(i);
            DepositoRelevante paradaSiguiente = paradas.get(i + 1);

            String coordsActual = paradaActual.deposito().getUbicacion().getLatitud() + ","
                    + paradaActual.deposito().getUbicacion().getLongitud();
            String coordsSiguiente = paradaSiguiente.deposito().getUbicacion().getLatitud() + ","
                    + paradaSiguiente.deposito().getUbicacion().getLongitud();

            // Llamada real a la API
            DistanciaDTO tramoIntermedioDist = ubicacionesServiceClient.obtenerDistancia(coordsActual, coordsSiguiente);
            Integer idParadaActual = paradaActual.deposito().getId();
            Integer idParadaSiguiente = paradaSiguiente.deposito().getId();

            Tramo tramoIntermedio = saveTramo(
                    null,
                    idParadaActual,
                    idParadaSiguiente,
                    TipoTramo.DEPOSITO_DEPOSITO,
                    tramoIntermedioDist);

            tramos.add(tramoIntermedio);
            kmTotales += tramoIntermedioDist.getKilometros();
            tiempoTotal = sumarTiempos(tiempoTotal, tramoIntermedioDist.getDuracionTexto());
        }

        // --- Último Tramo: Parada N -> Destino ---
        DepositoRelevante ultimaParada = paradas.get(paradas.size() - 1);
        DistanciaDTO tramoFinalDist = ultimaParada.tramoDepositoADestino();
        Integer idUltimaParada = ultimaParada.deposito().getId();

        Tramo tramoFinal = saveTramo(
                null,
                idUltimaParada,
                0, // Destino cliente, ID 0 (Placeholder)
                TipoTramo.DEPOSITO_DESTINO,
                tramoFinalDist);

        tramos.add(tramoFinal);
        kmTotales += tramoFinalDist.getKilometros();
        tiempoTotal = sumarTiempos(tiempoTotal, tramoFinalDist.getDuracionTexto());

        // 3. Crear y guardar la Ruta
        return saveRuta(tramos, kmTotales, tiempoTotal);
    }

    // ... (rest of the file remains unchanged, including generarPermutaciones)

    /**
     * Filtra la lista completa de depósitos y devuelve solo los que
     * están "razonablemente" en el camino (lógica "Santa Cruz vs Misiones").
     *
     * @param origenCoords  "lat,lon" de origen
     * @param destinoCoords "lat,lon" de destino
     * @return Una lista de depósitos relevantes, con sus distancias ya calculadas.
     */
    private List<DepositoRelevante> filtrarDepositosRelevantes(
            String origenCoords, String destinoCoords) {

        // 2a. Obtener distancia base para comparar
        DistanciaDTO infoDirecta;
        try {
            // Usamos el cliente para llamar al Microservicio de Ubicaciones
            infoDirecta = ubicacionesServiceClient.obtenerDistancia(origenCoords, destinoCoords);
        } catch (RestClientException e) {
            logger.error("Error CRÍTICO al calcular distancia directa: {}", e.getMessage());
            // Si esto falla, no podemos filtrar. Devolvemos una lista vacía.
            return List.of();
        }

        double distanciaDirectaKm = infoDirecta.getKilometros();

        // 2b. Definir la regla de negocio (Factor de Desvío)
        // Aceptamos depósitos que hagan la ruta hasta 100% más larga (x 2.0)
        double distanciaMaximaAceptable = distanciaDirectaKm * 2.0;

        // 2c. Obtener TODOS los depósitos
        List<DepositoDTO> todosLosDepositos;
        try {
            todosLosDepositos = ubicacionesServiceClient.obtenerDepositos();
        } catch (RestClientException e) {
            logger.error("Error CRÍTICO al obtener depósitos: {}", e.getMessage());
            return List.of(); // Si no hay depósitos, no hay nada que filtrar.
        }

        // 2d. Iniciar el filtrado
        List<DepositoRelevante> depositosRelevantes = new ArrayList<>();

        logger.info("--- FILTRANDO DEPÓSITOS ---");
        logger.info("Distancia Directa: {} km", distanciaDirectaKm);
        logger.info("Umbral Máximo de Desvío: {} km", distanciaMaximaAceptable);

        for (DepositoDTO deposito : todosLosDepositos) {
            // Obtenemos las coordenadas del depósito
            String depoCoords = deposito.getUbicacion().getLatitud() + "," + deposito.getUbicacion().getLongitud();

            try {
                // Calculamos el desvío (Hacemos 2 llamadas a la API)
                DistanciaDTO tramo1 = ubicacionesServiceClient.obtenerDistancia(origenCoords, depoCoords);
                DistanciaDTO tramo2 = ubicacionesServiceClient.obtenerDistancia(depoCoords, destinoCoords);

                double distanciaConDesvio = tramo1.getKilometros() + tramo2.getKilometros();

                if (distanciaConDesvio <= distanciaMaximaAceptable) {
                    logger.info("  RELEVANTE: {} (Desvío: {} km)", deposito.getNombre(), distanciaConDesvio);

                    // Guardamos los tramos para no recalcularlos después.
                    depositosRelevantes.add(new DepositoRelevante(deposito, tramo1, tramo2));
                } else {
                    logger.info("  DESCARTADO: {} (Desvío: {} km)", deposito.getNombre(), distanciaConDesvio);
                }

            } catch (RestClientException e) {
                // Si falla el cálculo para UN depósito (ej: Google no encuentra la ruta),
                // simplemente lo ignoramos y continuamos con el siguiente.
                logger.warn("Error calculando desvío para {}: {}", deposito.getNombre(), e.getMessage());
            }
        }

        logger.info("--- FIN FILTRADO. Depósitos relevantes: {} ---", depositosRelevantes.size());
        return depositosRelevantes;
    }

    /**
     * Clase auxiliar interna para guardar los depósitos filtrados
     * y los tramos pre-calculados.
     */
    private record DepositoRelevante(
            DepositoDTO deposito,
            DistanciaDTO tramoOrigenADeposito, // Tramo 1 (Origen -> Depósito)
            DistanciaDTO tramoDepositoADestino // Tramo 2 (Depósito -> Destino)
    ) {
    }

    /**
     * Devuelve todas las rutas sugeridas globales (sin solicitud asignada)
     */
    public List<Ruta> obtenerRutasSugeridas() {
        return rutaRepository.findBySolicitudIsNull();
    }

    /**
     * Obtiene una ruta por ID
     */
    public Ruta obtenerRutaPorId(Integer idRuta) {
        return rutaRepository.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
    }

    /**
     * Elimina una ruta por ID (solo si no tiene tramos con asignaciones activas)
     */
    public void eliminarRuta(Integer idRuta) {
        Ruta ruta = rutaRepository.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        rutaRepository.delete(ruta);
    }

    /**
     * Asigna una ruta a una solicitud.
     * En lugar de asignar directamente la ruta sugerida, se clona para que
     * múltiples
     * solicitudes puedan usar la misma ruta sugerida sin compartir datos.
     */
    /**
     * Asigna una ruta a una solicitud.
     * En lugar de asignar directamente la ruta sugerida, se clona para que
     * múltiples
     * solicitudes puedan usar la misma ruta sugerida sin compartir datos.
     * * @param fechaHoraInicioEstimada La fecha y hora en que se espera que
     * comience el primer tramo.
     */
    public Solicitud asignarRuta(Integer idSolicitud, Integer idRuta, java.time.LocalDateTime fechaHoraInicioEstimada) {

        Solicitud solicitud = solicitudService.getSolicitudEntityById(idSolicitud);

        Ruta rutaSugerida = rutaRepository.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Clonar la ruta sugerida para que sea independiente de otras asignaciones
        Ruta rutaAsignada = clonarRuta(rutaSugerida, solicitud);

        solicitud.setRutaAsignada(rutaAsignada);

        // 1. Calcular Costo Estimado
        double costoEstimado = calculoCostoService.calcularCostoEstimado(rutaAsignada);
        solicitud.setCostoEstimado(costoEstimado);

        // --- 2. CÁLCULO DE TIEMPOS ESTIMADOS Y FECHAS HORA ---

        // No necesitamos duracionTotal si usamos la diferencia entre fechas
        // java.time.Duration duracionTotal = java.time.Duration.ZERO;

        // La hora de fin del tramo anterior se inicializa con la hora de inicio de la
        // ruta
        java.time.LocalDateTime horaFinTramoAnterior = fechaHoraInicioEstimada;

        List<Tramo> tramos = rutaAsignada.getTramos();

        // Variables para capturar las fechas de los extremos
        java.time.LocalDateTime horaInicioPrimerTramo = fechaHoraInicioEstimada; // Ya conocido

        for (int i = 0; i < tramos.size(); i++) {
            Tramo tramo = tramos.get(i);
            java.time.Duration duracionTramo = parseDuration(tramo.getTiempo());

            java.time.LocalDateTime horaInicioActual;

            // LÓGICA DE RETRASO POR DEPÓSITO
            if (i == 0) {
                // El primer tramo (i=0) comienza en la hora proporcionada.
                horaInicioActual = fechaHoraInicioEstimada;
            } else {
                // Cualquier tramo subsiguiente (i > 0) comienza un día después
                // del fin del tramo anterior (para contabilizar la estadía).
                horaInicioActual = horaFinTramoAnterior.plusDays(1);
            }

            // Calcular hora de fin estimada: Inicio + Duración del tramo
            java.time.LocalDateTime horaFinEstimada = horaInicioActual.plus(duracionTramo);

            // Guardar las fechas en el tramo
            tramo.setFechaHoraInicioEstimada(horaInicioActual);
            tramo.setFechaHoraFinEstimada(horaFinEstimada);
            tramoRepository.save(tramo); // Persistir la actualización de fechas en cada tramo

            // Actualizar variables para el próximo tramo y la duración total
            horaFinTramoAnterior = horaFinEstimada;
            // duracionTotal = duracionTotal.plus(duracionTramo); // ELIMINAR esta línea
        }

        // 3. Calcular tiempo total para la Solicitud (Diferencia entre extremos)
        java.time.Duration duracionTotalEstimada = java.time.Duration.between(
                horaInicioPrimerTramo,
                horaFinTramoAnterior // horaFinTramoAnterior contiene la fecha de fin estimada del último tramo
        );

        long days = duracionTotalEstimada.toDays();
        long hours = duracionTotalEstimada.toHours() % 24;
        long minutes = duracionTotalEstimada.toMinutes() % 60;

        // Formato D:HH:MM
        solicitud.setTiempoEstimado(String.format("%d:%02d:%02d", days, hours, minutes));

        solicitudService.cambiarEstadoSolicitud(solicitud, "PROGRAMADA", "PROGRAMADA");

        // --- ¡NUEVO: Log de Asignación de Ruta! ---
        logger.info("RUTA ASIGNADA: Ruta ID [{}] (clonada de Ruta [{}]) asignada a Solicitud ID [{}]", 
            rutaAsignada.getId(),
            idRuta,
            idSolicitud
        );
        
        return solicitudService.actualizarSolicitud(solicitud);
    }

    /**
     * Clona una ruta sugerida con todos sus tramos asociados.
     * La ruta clonada se vincula a la solicitud específica.
     */
    private Ruta clonarRuta(Ruta rutaOriginal, Solicitud solicitud) {
        // Validar que la ruta original tiene tramos
        if (rutaOriginal.getTramos() == null || rutaOriginal.getTramos().isEmpty()) {
            throw new RuntimeException("La ruta sugerida no tiene tramos asociados");
        }

        // Crear nueva ruta
        Ruta rutaClonada = new Ruta();
        rutaClonada.setSolicitud(solicitud); // Vincular a la solicitud
        rutaClonada.setTiempoEstimado(rutaOriginal.getTiempoEstimado());

        // Guardar ruta clonada
        Ruta rutaGuardada = rutaRepository.save(rutaClonada);

        // Clonar todos los tramos de la ruta original
        java.util.List<Tramo> tramosClonados = new java.util.ArrayList<>();
        for (Tramo tramoOriginal : rutaOriginal.getTramos()) {
            Tramo tramoClonado = new Tramo();
            tramoClonado.setRuta(rutaGuardada);
            tramoClonado.setIdUbicacionOrigen(tramoOriginal.getIdUbicacionOrigen());
            tramoClonado.setIdUbicacionDestino(tramoOriginal.getIdUbicacionDestino());
            tramoClonado.setPatenteCamion(tramoOriginal.getPatenteCamion());
            tramoClonado.setTipoTramo(tramoOriginal.getTipoTramo());
            tramoClonado.setTiempo(tramoOriginal.getTiempo());
            tramoClonado.setDistanciaKm(tramoOriginal.getDistanciaKm());
            tramoClonado.setPatenteCamion(tramoOriginal.getPatenteCamion());
            tramoClonado.setIdDepositoDestino(tramoOriginal.getIdDepositoDestino());
            // Se pueden agregar más campos si es necesario
            tramoRepository.save(tramoClonado);
            tramosClonados.add(tramoClonado);
        }

        // Setear los tramos clonados en la ruta clonada (sin recargar)
        rutaGuardada.setTramos(tramosClonados);
        return rutaGuardada;
    }

    private java.time.Duration parseDuration(String tiempoEstimado) {
        if (tiempoEstimado == null || tiempoEstimado.isBlank()) {
            return java.time.Duration.ZERO;
        }

        try {
            if (tiempoEstimado.contains(":")) {
                // Lógica existente para formato D:HH:MM o HH:MM
                String[] parts = tiempoEstimado.split(":");
                if (parts.length == 3) {
                    long days = Long.parseLong(parts[0]);
                    long hours = Long.parseLong(parts[1]);
                    long minutes = Long.parseLong(parts[2]);
                    return java.time.Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
                } else if (parts.length == 2) {
                    long hours = Long.parseLong(parts[0]);
                    long minutes = Long.parseLong(parts[1]);
                    return java.time.Duration.ofHours(hours).plusMinutes(minutes);
                }
            } else {
                // Lógica robusta para formato de texto ("X hours Y mins")
                long hours = 0;
                long minutes = 0;

                // 1. Limpiar, convertir a minúsculas y asegurar que solo haya un espacio entre
                // palabras.
                String cleanedTime = tiempoEstimado.toLowerCase()
                        .replace("hours", " h ").replace("hour", " h ")
                        .replace("mins", " m ").replace("min", " m ")
                        .replace("days", " d ").replace("day", " d ")
                        .replace("and", " ").replaceAll("\\s+", " ").trim(); // Asegura un solo espacio

                // Ejemplo: "7 h 23 m"
                String[] parts = cleanedTime.split(" ");

                // 2. Iterar en pares (valor, unidad)
                for (int i = 0; i < parts.length - 1; i += 2) {
                    String value = parts[i];
                    String unit = parts[i + 1];

                    long num = Long.parseLong(value);

                    if (unit.equals("d")) {
                        hours += num * 24;
                    } else if (unit.equals("h")) {
                        hours += num;
                    } else if (unit.equals("m")) {
                        minutes += num;
                    }
                }
                return java.time.Duration.ofHours(hours).plusMinutes(minutes);
            }
        } catch (NumberFormatException e) {
            logger.error("Error al parsear la duración estimada: {}. Usando 0. Error: {}", // ⬅️ Corregido
                    tiempoEstimado, e.getMessage());
        }
        return java.time.Duration.ZERO;
    }

    // INICIO: NUESTRA LÓGICA (PASO 4)
    // ---

    /**
     * Genera todas las permutaciones de tamaño 'k' (cantidadDepositos)
     * a partir de una lista de depósitos relevantes.
     *
     * @param depositosRelevantes La lista filtrada de depósitos "en dirección".
     * @param k                   El número de paradas que solicitó el Operador.
     * @return Una lista de listas (ej: [[DepA, DepB], [DepB, DepA], [DepA, DepC],
     * ...])
     */
    private List<List<DepositoRelevante>> generarPermutaciones(
            List<DepositoRelevante> depositosRelevantes, int k) {

        List<List<DepositoRelevante>> todasLasPermutaciones = new ArrayList<>();

        // Empezamos el proceso recursivo con un camino vacío
        encontrarPermutacionesRecursivo(
                depositosRelevantes,
                k,
                new ArrayList<>(), // El "camino actual" (empieza vacío)
                todasLasPermutaciones // La lista donde guardamos los resultados
        );

        return todasLasPermutaciones;
    }

    /**
     * Método auxiliar recursivo (se llama a sí mismo) para construir los caminos.
     * Es la "vuelta atrás" (backtracking).
     */
    private void encontrarPermutacionesRecursivo(
            List<DepositoRelevante> listaOriginal,
            int k,
            List<DepositoRelevante> caminoActual,
            List<List<DepositoRelevante>> resultados) {

        // --- 1. Caso Base: ¿El camino está completo? ---
        // Si k=2 y nuestro camino ya tiene 2 paradas (ej: [DepA, DepC]),
        // lo guardamos en los resultados y terminamos esta rama.
        if (caminoActual.size() == k) {
            resultados.add(new ArrayList<>(caminoActual)); // Guardamos una copia
            return;
        }

        // --- 2. Caso Recursivo: Seguir construyendo el camino ---

        // Iteramos sobre TODOS los depósitos relevantes
        for (DepositoRelevante deposito : listaOriginal) {

            // Si el depósito que estamos mirando (ej: DepA)
            // NO está ya en el camino que estamos construyendo...
            if (!caminoActual.contains(deposito)) {

                // ...lo añadimos al camino
                caminoActual.add(deposito); // (El camino ahora es [DepA])

                // Llamamos a esta MISMA función para encontrar el siguiente paso
                // (Ej: "buscar el segundo paso para el camino [DepA]")
                encontrarPermutacionesRecursivo(listaOriginal, k, caminoActual, resultados);

                // --- 3. La "Vuelta Atrás" (Backtracking) ---
                // Cuando la llamada anterior termina (ej: encontró [DepA, DepB]),
                // "borramos" el último paso.
                caminoActual.remove(caminoActual.size() - 1);
                // (El camino vuelve a ser [DepA]).
                // En la siguiente iteración del 'for', probará con DepC
                // (para armar [DepA, DepC]).
            }
        }
    }

    // ---
    // FIN: NUESTRA LÓGICA (PASO 4)

    /**
     * Convierte el tiempo estimado en formato "D:HH:MM" o "HH:MM" a minutos
     * totales.
     *
     * @param tiempoEstimado Tiempo en formato "D:HH:MM" o "HH:MM".
     * @return Minutos totales, o 0 si el formato es inválido o nulo.
     */
    private int parseTiempoEstimado(String tiempoEstimado) {
        if (tiempoEstimado == null || tiempoEstimado.isBlank() || !tiempoEstimado.contains(":")) {
            return 0;
        }

        try {
            String[] parts = tiempoEstimado.split(":");
            int totalMinutes = 0;

            if (parts.length == 3) {
                // Formato: DIAS:HORAS:MINUTOS
                int days = Integer.parseInt(parts[0]);
                int hours = Integer.parseInt(parts[1]);
                int minutes = Integer.parseInt(parts[2]);

                totalMinutes += days * 24 * 60; // Días a minutos
                totalMinutes += hours * 60; // Horas a minutos
                totalMinutes += minutes; // Minutos

            } else if (parts.length == 2) {
                // Formato: HORAS:MINUTOS (asumiendo que las horas pueden ser > 24)
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                totalMinutes += hours * 60; // Horas a minutos
                totalMinutes += minutes; // Minutos

            } else {
                // Formato no reconocido
                return 0;
            }

            return totalMinutes;

        } catch (NumberFormatException e) {
            logger.error("Error al parsear el tiempo estimado ({}). Verifique que todas las partes sean números. Error: {}", // ⬅️ Corregido
                    tiempoEstimado, e.getMessage());
            return 0;
        }
    }
}