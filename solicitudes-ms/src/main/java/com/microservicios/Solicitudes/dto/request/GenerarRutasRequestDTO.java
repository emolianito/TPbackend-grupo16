package com.microservicios.Solicitudes.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
public class GenerarRutasRequestDTO {

  @NotBlank(message = "La dirección de origen no puede estar vacía.")
  private String origenDireccion; // ANTES: origenCoords

  @NotBlank(message = "La dirección de destino no puede estar vacía.")
  private String destinoDireccion; // ANTES: destinoCoords

  @Min(value = 0, message = "La cantidad de depósitos debe ser 0 o mayor")
  private int cantidadDepositos;
}