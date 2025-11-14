package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.microservicios.Solicitudes.dto.request.CrearTarifaDTO;
import com.microservicios.Solicitudes.entity.Tarifa;
import com.microservicios.Solicitudes.services.TarifaService;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tarifas")
@AllArgsConstructor
public class TarifaController {
    
    private final TarifaService tarifaService;

    @PostMapping
    public ResponseEntity<Tarifa> crearTarifa(@RequestBody CrearTarifaDTO tarifa) {
        Tarifa nuevaTarifa = tarifaService.crearTarifa(tarifa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarifa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtenerTarifa(@PathVariable Integer id) {
        Tarifa tarifa = tarifaService.obtenerTarifa(id);
        return ResponseEntity.ok(tarifa);
    }

    @GetMapping
    public ResponseEntity<List<Tarifa>> obtenerTodasLasTarifas() {
        List<Tarifa> tarifas = tarifaService.obtenerTodasLasTarifas();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/vigente")
    public ResponseEntity<Tarifa> getVigente() {
        Tarifa tarifaVigente = tarifaService.getTarifaVigente();
        return ResponseEntity.ok(tarifaVigente);
    }
}
