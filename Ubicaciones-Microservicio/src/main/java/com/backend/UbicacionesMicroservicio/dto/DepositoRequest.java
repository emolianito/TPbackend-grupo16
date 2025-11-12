package com.backend.UbicacionesMicroservicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepositoRequest {

  @NotBlank(message = "El nombre del depósito no puede estar vacío")
  private String nombre;

  @NotBlank(message = "La dirección no puede estar vacía")
  private String direccion;

  @NotNull(message = "El costo por día de estadía no puede ser nulo")
  @Positive(message = "El costo por día de estadía debe ser mayor a 0")
  private Float costoDiaEstadia;
}