package com.microservicio.transporte.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class crearTransportistaDto {

    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

}
