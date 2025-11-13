package com.microservicios.Solicitudes.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;


import com.microservicios.Solicitudes.entity.EstadoSolicitud;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Solicitud;
import com.microservicios.Solicitudes.entity.TipoTramo;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.RutaRepository;
import com.microservicios.Solicitudes.repository.TramoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final SolicitudService solicitudService;
    private final CalculoCostoService calculoCostoService;

    /**
     * Genera 3 rutas sugeridas dinámicamente según origen y destino.
     * Las rutas se crean sin asignación a solicitud específica (solicitud = null).
     * Pueden ser clonadas posteriormente cuando se asignan a una solicitud.
     */
    public List<Ruta> generarRutasSugeridas(Integer idUbicacionOrigen, Integer idUbicacionDestino) {
        // En el futuro, validar que las ubicaciones existan en el microservicio de ubicaciones
        if (idUbicacionOrigen == null || idUbicacionDestino == null) {
            throw new RuntimeException("Ubicación origen y destino son requeridas");
        }

        if (idUbicacionOrigen.equals(idUbicacionDestino)) {
            throw new RuntimeException("Origen y destino no pueden ser iguales");
        }

        List<Ruta> rutas = new ArrayList<>();

        // Ruta 1: Directa (1 tramo)
        Ruta r1 = crearRutaDirecta(idUbicacionOrigen, idUbicacionDestino);
        rutas.add(r1);

        // Ruta 2: Con 1 parada intermedia (2 tramos)
        Ruta r2 = crearRutaConUnaParada(idUbicacionOrigen, idUbicacionDestino);
        rutas.add(r2);

        // Ruta 3: Con 2 paradas intermedias (3 tramos)
        Ruta r3 = crearRutaConDosParadas(idUbicacionOrigen, idUbicacionDestino);
        rutas.add(r3);

        return rutas;
    }

    /**
     * Crea una ruta directa de origen a destino (1 tramo)
     */
    private Ruta crearRutaDirecta(Integer origen, Integer destino) {
        Ruta ruta = new Ruta();
        ruta.setSolicitud(null);  // Ruta global sin solicitud específica
        ruta.setCostoEstimado(45000.0);
        ruta.setTiempoEstimado("04:30");
        rutaRepository.save(ruta);

        Tramo tramo = new Tramo();
        tramo.setRuta(ruta);
        tramo.setIdUbicacionOrigen(origen);
        tramo.setIdUbicacionDestino(destino);
        tramo.setPatenteCamion("1234-ABC");
        tramo.setTipoTramo(TipoTramo.DEPOSITO_DESTINO);
        tramo.setCostoAproximado(45000.0);
        tramo.setTiempo("04:30");
        tramoRepository.save(tramo);

        return ruta;
    }

    /**
     * Crea una ruta con 1 parada intermedia (2 tramos)
     */
    private Ruta crearRutaConUnaParada(Integer origen, Integer destino) {
        Ruta ruta = new Ruta();
        ruta.setSolicitud(null);
        ruta.setCostoEstimado(52500.0);
        ruta.setTiempoEstimado("05:30");
        rutaRepository.save(ruta);

        // Parada intermedia mock (simulamos ubicación central)
        Integer parada1 = determinarParadaIntermedia(origen, destino, 1);

        // Tramo 1: Origen → Parada
        Tramo tramo1 = new Tramo();
        tramo1.setRuta(ruta);
        tramo1.setIdUbicacionOrigen(origen);
        tramo1.setIdUbicacionDestino(parada1);
        tramo1.setPatenteCamion("1234-ABC");
        tramo1.setTipoTramo(TipoTramo.ORIGEN_DEPOSITO);
        tramo1.setCostoAproximado(13500.0);
        tramo1.setTiempo("01:30");
        tramoRepository.save(tramo1);

        // Tramo 2: Parada → Destino
        Tramo tramo2 = new Tramo();
        tramo2.setRuta(ruta);
        tramo2.setIdUbicacionOrigen(parada1);
        tramo2.setIdUbicacionDestino(destino);
        tramo2.setPatenteCamion("5678-DEF");
        tramo2.setTipoTramo(TipoTramo.DEPOSITO_DESTINO);
        tramo2.setCostoAproximado(39000.0);
        tramo2.setTiempo("04:00");
        tramoRepository.save(tramo2);

        return ruta;
    }

    /**
     * Crea una ruta con 2 paradas intermedias (3 tramos)
     */
    private Ruta crearRutaConDosParadas(Integer origen, Integer destino) {
        Ruta ruta = new Ruta();
        ruta.setSolicitud(null);
        ruta.setCostoEstimado(48000.0);
        ruta.setTiempoEstimado("05:00");
        rutaRepository.save(ruta);

        Integer parada1 = determinarParadaIntermedia(origen, destino, 1);
        Integer parada2 = determinarParadaIntermedia(origen, destino, 2);

        // Tramo 1: Origen → Parada1
        Tramo tramo1 = new Tramo();
        tramo1.setRuta(ruta);
        tramo1.setIdUbicacionOrigen(origen);
        tramo1.setIdUbicacionDestino(parada1);
        tramo1.setPatenteCamion("1234-ABC");
        tramo1.setTipoTramo(TipoTramo.ORIGEN_DEPOSITO);
        tramo1.setCostoAproximado(34500.0);
        tramo1.setTiempo("03:30");
        tramoRepository.save(tramo1);

        // Tramo 2: Parada1 → Parada2 (intermedio)
        Tramo tramo2 = new Tramo();
        tramo2.setRuta(ruta);
        tramo2.setIdUbicacionOrigen(parada1);
        tramo2.setIdUbicacionDestino(parada2);
        tramo2.setPatenteCamion("5678-DEF");
        tramo2.setTipoTramo(TipoTramo.DESSTINO_DEPOSITO);
        tramo2.setCostoAproximado(10000.0);
        tramo2.setTiempo("01:00");
        tramoRepository.save(tramo2);

        // Tramo 3: Parada2 → Destino
        Tramo tramo3 = new Tramo();
        tramo3.setRuta(ruta);
        tramo3.setIdUbicacionOrigen(parada2);
        tramo3.setIdUbicacionDestino(destino);
        tramo3.setPatenteCamion("1234-ABC");
        tramo3.setTipoTramo(TipoTramo.DEPOSITO_DESTINO);
        tramo3.setCostoAproximado(13500.0);
        tramo3.setTiempo("01:30");
        tramoRepository.save(tramo3);

        return ruta;
    }

    /**
     * Determina paradas intermedias mockeadas según origen/destino
     */
    private Integer determinarParadaIntermedia(Integer origen, Integer destino, int numeroParada) {
        // Mock simple: retorna ubicación intermedia basada en origen/destino
        // En futuro, esto vendría del microservicio de ubicaciones
        if (numeroParada == 1) {
            return 4;  // Zárate
        } else {
            return 5;  // San Nicolás
        }
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
     * En lugar de asignar directamente la ruta sugerida, se clona para que múltiples
     * solicitudes puedan usar la misma ruta sugerida sin compartir datos.
     */
    public Solicitud asignarRuta(Integer idSolicitud, Integer idRuta) {

        Solicitud solicitud = solicitudService.getSolicitudEntityById(idSolicitud);

        Ruta rutaSugerida = rutaRepository.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Clonar la ruta sugerida para que sea independiente de otras asignaciones
        Ruta rutaAsignada = clonarRuta(rutaSugerida, solicitud);

        solicitud.setRutaAsignada(rutaAsignada);
        solicitud.setEstado(EstadoSolicitud.EN_RUTA);

        double costoEstimado = calculoCostoService.calcularCostoEstimado(rutaAsignada);
        solicitud.setCostoEstimado(costoEstimado);

        // Calcular tiempo total acumulando tiempos de tramos
        solicitud.setTiempoEstimado(
                rutaAsignada.getTramos().stream()
                        .map(Tramo::getTiempo)
                        .reduce((t1, t2) -> {
                            String[] parts1 = t1.split(":");
                            String[] parts2 = t2.split(":");
                            int hours = Integer.parseInt(parts1[0]) + Integer.parseInt(parts2[0]);
                            int minutes = Integer.parseInt(parts1[1]) + Integer.parseInt(parts2[1]);
                            if (minutes >= 60) {
                                hours += minutes / 60;
                                minutes = minutes % 60;
                            }
                            return String.format("%02d:%02d", hours, minutes);
                        }).orElse("00:00")
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
        rutaClonada.setSolicitud(solicitud);  // Vincular a la solicitud
        rutaClonada.setCostoEstimado(rutaOriginal.getCostoEstimado());
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
            tramoClonado.setCostoAproximado(tramoOriginal.getCostoAproximado());
            tramoClonado.setTiempo(tramoOriginal.getTiempo());
            // Se pueden agregar más campos si es necesario
            tramoRepository.save(tramoClonado);
            tramosClonados.add(tramoClonado);
        }

        // Setear los tramos clonados en la ruta clonada (sin recargar)
        rutaGuardada.setTramos(tramosClonados);
        return rutaGuardada;
    }
}