package com.microservicios.Solicitudes.dto.external;

import lombok.Data;

@Data
public class UbicacionDTO {
  private int id;
  private String direccion;
  private float latitud;
  private float longitud;
}