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

import com.microservicios.Solicitudes.dto.request.GenerarRutasDTO;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.services.RutaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    /**
     * Genera rutas sugeridas entre una ubicación origen y destino.
     * Devuelve 3 rutas alternativas con diferentes tramos.
     */
    @PostMapping("/sugerir")
    public ResponseEntity<List<Ruta>> generarRutas(@RequestBody GenerarRutasDTO dto) {
        List<Ruta> rutas = rutaService.generarRutasSugeridas(
                dto.getIdUbicacionOrigen(),
                dto.getIdUbicacionDestino()
        );
        return ResponseEntity.ok(rutas);
    }

    /**
     * Obtiene todas las rutas sugeridas globales (sin solicitud asignada).
     */
    @GetMapping
    public ResponseEntity<List<Ruta>> obtenerRutas() {
        List<Ruta> rutas = rutaService.obtenerRutasSugeridas();
        return ResponseEntity.ok(rutas);
    }

    /**
     * Obtiene una ruta específica por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerRutaPorId(@PathVariable Integer id) {
        Ruta ruta = rutaService.obtenerRutaPorId(id);
        return ResponseEntity.ok(ruta);
    }

    /**
     * Elimina una ruta por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Integer id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}
