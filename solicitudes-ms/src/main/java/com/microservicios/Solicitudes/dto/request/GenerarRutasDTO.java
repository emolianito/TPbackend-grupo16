package com.microservicios.Solicitudes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarRutasDTO {
    
    private Integer idUbicacionOrigen;
    private Integer idUbicacionDestino;
    private Integer cantidadRutas = 3;  // Default 3 rutas alternativas
}
