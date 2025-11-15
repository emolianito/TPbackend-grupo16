package com.microservicios.Solicitudes.dto.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class RutaTentativaDTO implements Comparable<RutaTentativaDTO> {

  private List<TramoTentativoDTO> tramos;
  private Double kilometrosTotales;
  private int cantidadParadas;

  // Constructor para facilitar la vida
  public RutaTentativaDTO(List<TramoTentativoDTO> tramos, Double kilometrosTotales) {
    this.tramos = tramos;
    this.kilometrosTotales = kilometrosTotales;
    this.cantidadParadas = tramos.size() - 1; // 1 tramo = 0 paradas; 2 tramos = 1 parada
  }

  // ¡Importante! Esto nos permitirá ordenar las rutas
  // para mostrar las más cortas (más "en dirección") primero.
  @Override
  public int compareTo(RutaTentativaDTO otra) {
    return Double.compare(this.kilometrosTotales, otra.kilometrosTotales);
  }
}