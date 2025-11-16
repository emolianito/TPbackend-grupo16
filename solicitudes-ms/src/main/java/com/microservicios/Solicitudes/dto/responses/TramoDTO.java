package com.microservicios.Solicitudes.dto.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TramoDTO {
    private Integer id;
    private String patenteCamion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipoTramo;
    private String mensaje;
}