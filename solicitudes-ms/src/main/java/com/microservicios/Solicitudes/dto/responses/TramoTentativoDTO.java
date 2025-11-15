package com.microservicios.Solicitudes.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoTentativoDTO {

  private String origen; // Coordenadas o nombre
  private String destino; // Coordenadas o nombre
  private String tipoTramo; // "ORIGEN-DESTINO", "ORIGEN-DEPOSITO", etc.
  private Double kilometros;
  private String duracionTexto;
}

