package com.microservicios.Solicitudes.dto.responses;

import java.time.LocalDate;
import com.microservicios.Solicitudes.entity.EstadoSolicitud;
import com.microservicios.Solicitudes.entity.Ruta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudDTO {

    private Integer id;
    private Integer idContenedor;
    private String dniCliente;
    private Double costoEstimado;
    private Double costoReal;
    private String tiempoEstimado;
    private String tiempoReal;
    private Ruta rutaAsignada;
    private EstadoSolicitud estado;
    private LocalDate fechaSolicitud;

}
