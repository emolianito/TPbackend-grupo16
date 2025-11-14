package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Tarifa crearTarifa(@RequestBody CrearTarifaDTO tarifa) {
        return tarifaService.crearTarifa(tarifa);
    }

    @GetMapping("/{id}")
    public Tarifa obtenerTarifa(@PathVariable Integer id) {
        return tarifaService.obtenerTarifa(id);
    }

    @GetMapping
    public List<Tarifa> obtenerTodasLasTarifas() {
        return tarifaService.obtenerTodasLasTarifas();
    }

    @GetMapping("/vigente")
    public Tarifa getVigente() {
        return tarifaService.getTarifaVigente();
    }
}
