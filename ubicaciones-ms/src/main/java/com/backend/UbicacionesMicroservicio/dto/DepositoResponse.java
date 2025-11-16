package com.backend.UbicacionesMicroservicio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositoResponse {
  private int id;
  private String nombre;
  private String direccion;
  private float latitud;
  private float longitud;
  private float costoDiaEstadia; // NUEVO
}