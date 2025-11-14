package com.microservicios.Solicitudes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.Solicitudes.dto.request.AsignacionCamionDTO;
import com.microservicios.Solicitudes.services.TramoService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/solicitudes/tramos")
@RequiredArgsConstructor
public class TramoController {

    private final TramoService tramoService;

    @PatchMapping("/{idTramo}/asignacion")
    public ResponseEntity<Void> asignarCamion(
            @PathVariable Integer idTramo,
            @RequestBody AsignacionCamionDTO dto) {
        tramoService.asignarCamion(idTramo, dto.getIdCamion());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idTramo}/inicio")
    public ResponseEntity<Void> iniciarTramo(@PathVariable Integer idTramo) {
        tramoService.iniciarTramo(idTramo);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idTramo}/fin")
    public ResponseEntity<Void> finalizarTramo(@PathVariable Integer idTramo) {
        tramoService.finalizarTramo(idTramo);
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un tramo por ID
     */
    @DeleteMapping("/{idTramo}")
    public ResponseEntity<Void> eliminarTramo(@PathVariable Integer idTramo) {
        tramoService.eliminarTramo(idTramo);
        return ResponseEntity.noContent().build();
    }
}