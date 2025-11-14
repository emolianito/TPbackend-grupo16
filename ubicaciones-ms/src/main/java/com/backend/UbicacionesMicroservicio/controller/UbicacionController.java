package com.backend.UbicacionesMicroservicio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.UbicacionesMicroservicio.entity.Ubicacion;
import com.backend.UbicacionesMicroservicio.service.GeocodingService;
import com.backend.UbicacionesMicroservicio.service.UbicacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ubicaciones/localizaciones")
public class UbicacionController {
  private final UbicacionService ubicacionService;
  private final GeocodingService geocodingService;

  public UbicacionController(UbicacionService ubicacionService, GeocodingService geocodingService) {
    this.ubicacionService = ubicacionService;
    this.geocodingService = geocodingService;
  }

  @PostMapping
  public ResponseEntity<Ubicacion> createUbicacion(@Valid @RequestBody Ubicacion ubicacion) {
    Ubicacion created = ubicacionService.createUbicacion(ubicacion);
    return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
  }

  // NUEVO: Crear ubicación desde dirección
  @PostMapping("/desde-direccion")
  public ResponseEntity<?> createUbicacionDesdeDireccion(@RequestParam String direccion) {
    try {
      String direccionLimpia = direccion.trim().replaceAll("\\s+", " ");
      GeocodingService.Coordenadas coords = geocodingService.obtenerCoordenadas(direccion);

      Ubicacion ubicacion = new Ubicacion();
      ubicacion.setDireccion(direccion);
      ubicacion.setLatitud(coords.getLatitud());
      ubicacion.setLongitud(coords.getLongitud());

      Ubicacion created = ubicacionService.createUbicacion(ubicacion);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error al geocodificar: " + e.getMessage());
    }
  }

  // NUEVO: Obtener coordenadas de una dirección
  @GetMapping("/geocode")
  public ResponseEntity<?> geocodeDireccion(@RequestParam String direccion) {
    try {
      GeocodingService.Coordenadas coords = geocodingService.obtenerCoordenadas(direccion);
      return ResponseEntity.ok(coords);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Ubicacion> getUbicacionById(@PathVariable int id) {
    Ubicacion ubicacion = ubicacionService.getUbicacionById(id);
    if (ubicacion != null) {
      return ResponseEntity.ok(ubicacion); // 200 OK
    }
    return ResponseEntity.notFound().build(); // 404 Not Found
  }

  @GetMapping
  public ResponseEntity<List<Ubicacion>> getAllUbicacion() {
    List<Ubicacion> ubicaciones = ubicacionService.getAllUbicacion();
    return ResponseEntity.ok(ubicaciones); // 200 OK
  }
}