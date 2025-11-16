package com.microservicios.Solicitudes.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.microservicios.Solicitudes.dto.request.AsignacionCamionDTO;
import com.microservicios.Solicitudes.dto.responses.TramoDTO;
import com.microservicios.Solicitudes.services.TramoService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/solicitudes/tramos")
@RequiredArgsConstructor
public class TramoController {

    private final TramoService tramoService;

    //TODO: PASARLE EL ID DEL CAMION EN LA URL MEJOR??
    @PatchMapping("/{idTramo}/asignacion")
    public ResponseEntity<TramoDTO> asignarCamion(
            @PathVariable Integer idTramo,
            @RequestBody AsignacionCamionDTO dto) {
        TramoDTO tramo = tramoService.asignarCamion(idTramo, dto.getIdCamion());
        return ResponseEntity.ok(tramo);
    }

    @PatchMapping("/{idTramo}/inicio")
    public ResponseEntity<TramoDTO> iniciarTramo(@PathVariable Integer idTramo) {
        TramoDTO tramo = tramoService.iniciarTramo(idTramo);
        return ResponseEntity.ok(tramo);
    }

    @PatchMapping("/{idTramo}/fin")
    public ResponseEntity<TramoDTO> finalizarTramo(@PathVariable Integer idTramo) {
        TramoDTO tramo = tramoService.finalizarTramo(idTramo);
        return ResponseEntity.ok(tramo);
    }

    //TODO: NO CREO QUE SEA COHERENTE PERMITIR ELIMINAR TRAMOS
    @DeleteMapping("/{idTramo}")
    public ResponseEntity<Void> eliminarTramo(@PathVariable Integer idTramo) {
        tramoService.eliminarTramo(idTramo);
        return ResponseEntity.noContent().build();
    }
}