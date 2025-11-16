package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.services.RutaService;
import com.microservicios.Solicitudes.services.SolicitudService;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final RutaService rutaService;

    public SolicitudController(SolicitudService solicitudService, RutaService rutaService) {
        this.solicitudService = solicitudService;
        this.rutaService = rutaService;
    }

    @GetMapping("/hola")
    public String hola() {
        return "Microservicio Solicitudes funcionando ✅";
    }

    @PostMapping
    public SolicitudDTO crearSolicitud(@RequestBody CrearSolicitudDTO dto) {
        return solicitudService.getSolicitudById(solicitudService.createSolicitud(dto).getId());
    }

    @GetMapping("/{id}")
    public SolicitudDTO obtenerSolicitudById(@PathVariable Integer id) {
        return solicitudService.getSolicitudById(id);
    }

    @GetMapping
    public List<SolicitudDTO> obtenerTodasLasSolicitudes() {
        return solicitudService.getAllSolicitudes();
    }

    /**
     * Asigna una ruta (sugerida) a una solicitud.
     * La ruta sugerida se clona para que la solicitud tenga su propia copia independiente.
     */
    @PostMapping("/{id}/asignar-ruta/{rutaId}")
    public ResponseEntity<?> asignarRuta(@PathVariable Integer id, @PathVariable Integer rutaId) {
        return ResponseEntity.ok(rutaService.asignarRuta(id, rutaId));
    }

    /**
     * Elimina una solicitud por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Integer id) {
        solicitudService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}