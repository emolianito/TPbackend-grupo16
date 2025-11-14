package com.microservicios.Solicitudes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudDTO {
    
    private Integer idCliente;
    private Integer idContenedor;
}
