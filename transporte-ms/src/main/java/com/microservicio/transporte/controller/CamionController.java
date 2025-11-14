package com.microservicio.transporte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.transporte.dto.request.crearCamionDto;
import com.microservicio.transporte.dto.responses.CamionDto;
import com.microservicio.transporte.entity.Camion;
import com.microservicio.transporte.service.CamionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/camiones")
public class CamionController {
    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @PostMapping
    public ResponseEntity<CamionDto> createCamion (@RequestBody crearCamionDto camion) {
        CamionDto dto = camionService.crearCamion(camion);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public List<CamionDto> listar() {
        return camionService.listarCamiones();
    }

    @GetMapping("/disponibles")
    public List<CamionDto> listarDisponibles() {
        return camionService.listarDisponibles();
    }

    @GetMapping("/{patente}")
    public CamionDto getByPatente(@PathVariable String patente) {
        return camionService.buscarPorPatente(patente);
    }

    @DeleteMapping("/{patente}")
    public ResponseEntity<Void> deleteByPatente(@PathVariable String patente) {
        camionService.eliminarPorPatente(patente);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{patente}/ocupar")
    public ResponseEntity<CamionDto> ocuparCamion(@PathVariable String patente) {
        CamionDto dto = camionService.ocuparCamion(patente);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
    
    @PatchMapping("/{patente}/liberar")
    public ResponseEntity<CamionDto> liberarCamion(@PathVariable String patente) {
        CamionDto dto = camionService.liberarCamion(patente);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/{patente}/comprobar-capacidad")
    public ResponseEntity<Boolean> comprobarCapacidad(@PathVariable String patente, @RequestBody Integer idContenedor) {
        return ResponseEntity.ok(camionService.comprobarCapacidad(patente, idContenedor));
    }
}
