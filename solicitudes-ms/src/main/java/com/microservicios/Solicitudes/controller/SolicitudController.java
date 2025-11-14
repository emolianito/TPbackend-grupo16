package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import com.microservicios.Solicitudes.dto.request.CrearSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.CambioEstadoSolicitudDTO;
import com.microservicios.Solicitudes.dto.responses.SolicitudDTO;
import com.microservicios.Solicitudes.services.RutaService;
import com.microservicios.Solicitudes.services.SolicitudService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/solicitudes")
@AllArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final RutaService rutaService;


    @PostMapping
    public ResponseEntity<SolicitudDTO> crearSolicitud(@RequestBody CrearSolicitudDTO dto) {
        SolicitudDTO solicitudDTO = solicitudService.getSolicitudById(solicitudService.createSolicitud(dto).getId());   
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudDTO);
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

    @GetMapping("/{id}/seguimiento")
    public List<CambioEstadoSolicitudDTO> obtenerSeguimientoSolicitud(@PathVariable Integer id) {
        return solicitudService.getCambiosEstadoSolicitud(id);
    }   
}