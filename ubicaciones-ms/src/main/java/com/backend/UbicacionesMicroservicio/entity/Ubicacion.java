package com.backend.UbicacionesMicroservicio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "ubicaciones")
public class Ubicacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotBlank(message = "La dirección no puede estar vacía")
  @Column(nullable = false)
  private String direccion;

  @NotNull(message = "La latitud no puede ser nula")
  @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
  @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
  @Column(nullable = false)
  private float latitud;

  @NotNull(message = "La longitud no puede ser nula")
  @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
  @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
  @Column(nullable = false)
  private float longitud;
}