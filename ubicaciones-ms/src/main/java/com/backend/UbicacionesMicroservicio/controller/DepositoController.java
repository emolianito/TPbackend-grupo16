package com.backend.UbicacionesMicroservicio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.UbicacionesMicroservicio.dto.ContenedorDTO;
import com.backend.UbicacionesMicroservicio.dto.DepositoRequest;
import com.backend.UbicacionesMicroservicio.entity.Deposito;
import com.backend.UbicacionesMicroservicio.service.ContenedorClientService;
import com.backend.UbicacionesMicroservicio.service.DepositoService;

@RestController
@RequestMapping("/ubicaciones/depositos")
public class DepositoController {
  private final DepositoService depositoService;
  private final ContenedorClientService contenedorClientService;

  public DepositoController(DepositoService depositoService, ContenedorClientService contenedorClientService) {
    this.depositoService = depositoService;
    this.contenedorClientService = contenedorClientService;
  }

  @PostMapping
  public ResponseEntity<Deposito> createDeposito(@RequestBody DepositoRequest deposito) {
    Deposito created = depositoService.createDeposito(deposito);
    return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
  }

  @GetMapping("/{id}")
  public ResponseEntity<Deposito> getDepositoById(@PathVariable int id) {
    Deposito deposito = depositoService.getDepositoById(id);
    if (deposito != null) {
      return ResponseEntity.ok(deposito); // 200 OK
    }
    return ResponseEntity.notFound().build(); // 404 Not Found
  }

  @GetMapping
  public ResponseEntity<List<Deposito>> getAllDepositos() {
    List<Deposito> depositos = depositoService.getAllDepositos();
    return ResponseEntity.ok(depositos); // 200 OK
  }

  @PutMapping("/{id}")
  public ResponseEntity<Deposito> updateDeposito(
      @PathVariable int id,
      @RequestBody DepositoRequest deposito) {
    Deposito updated = depositoService.updateDeposito(id, deposito);
    if (updated != null) {
      return ResponseEntity.ok(updated); // 200 OK
    }
    return ResponseEntity.notFound().build(); // 404 Not Found
  }

  @GetMapping("/{id}/contenedores")
  public ResponseEntity<List<ContenedorDTO>> obtenerContenedoresDelDeposito(@PathVariable int id) {
    List<ContenedorDTO> contenedores = contenedorClientService.getContenedoresByDeposito(id);
    return ResponseEntity.ok(contenedores);
  }

}
