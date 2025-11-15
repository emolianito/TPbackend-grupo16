package com.microservicios.Solicitudes.dto.responses; 

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoContenedorDTO {
    
    private Integer id;
    private String nombre;
}