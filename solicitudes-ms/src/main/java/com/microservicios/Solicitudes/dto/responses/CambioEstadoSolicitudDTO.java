package com.microservicios.Solicitudes.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CambioEstadoSolicitudDTO {

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estadoSolicitud; // vemos como sale
    private String estadoContenedor;


}