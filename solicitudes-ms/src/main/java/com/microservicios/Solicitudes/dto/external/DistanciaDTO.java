package com.microservicios.Solicitudes.dto.external;

import lombok.Data;

// Esta clase debe ser un espejo de la que existe en el 
// microservicio de Ubicaciones
@Data
public class DistanciaDTO {
  private String origen;
  private String destino;
  private Double kilometros;
  private String duracionTexto;
}