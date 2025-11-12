package com.backend.UbicacionesMicroservicio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.backend.UbicacionesMicroservicio.dto.DistanciaDTO;
import com.backend.UbicacionesMicroservicio.service.GeoService;

@RestController
@RequestMapping("/api/distancia")
@RequiredArgsConstructor
public class GeoController {
  private final GeoService geoService;

  @GetMapping
  public DistanciaDTO obtenerDistancia(
      @RequestParam String origen,
      @RequestParam String destino) throws Exception {
    return geoService.calcularDistancia(origen, destino);
  }
}
