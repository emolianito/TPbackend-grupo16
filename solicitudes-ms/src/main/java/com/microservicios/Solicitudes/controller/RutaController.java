package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.microservicios.Solicitudes.dto.request.GenerarRutasDTO;
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.services.RutaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/rutas")
@AllArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    //TODO: DEFINIR COMO SE CREAN LAS RUTAS SUGERIDAS
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
        return ResponseEntity.status(HttpStatus.CREATED).body(rutas);
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
