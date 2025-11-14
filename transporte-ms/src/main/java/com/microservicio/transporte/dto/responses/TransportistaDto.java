package com.microservicio.transporte.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaDto {

    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

}
