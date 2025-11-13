package com.microservicio.transporte.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.transporte.dto.request.crearTransportistaDto;
import com.microservicio.transporte.dto.responses.TransportistaDto;
import com.microservicio.transporte.service.TransportistaService;

@RestController
@RequestMapping("/transportistas")
public class TransportistaController {

    private final TransportistaService transportistaService;

    public TransportistaController(TransportistaService transportistaService) {
        this.transportistaService = transportistaService;
    }

    @PostMapping
    public ResponseEntity<TransportistaDto> crear(@RequestBody crearTransportistaDto dto) {
        TransportistaDto created = transportistaService.crearTransportista(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<TransportistaDto> listar() {
        return transportistaService.listarTransportistas();
    }

    @GetMapping("/{dni}")
    public TransportistaDto getByDni(@PathVariable String dni) {
        return transportistaService.buscarPorDni(dni);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminar(@PathVariable String dni) {
        transportistaService.eliminarPorDni(dni);
        return ResponseEntity.noContent().build();
    }

}
