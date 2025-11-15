package com.microservicios.Solicitudes.dto.external;

import lombok.Data;

// Esta clase es un espejo de GeocodingService.Coordenadas
//
@Data
public class CoordenadasDTO {
  private float latitud;
  private float longitud;
}