package com.microservicios.Solicitudes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class CambioEstadoSolicitud {
    
    @Id
    private Integer id;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estadoSolicitud; // vemos como sale
    private String estadoContenedor;

    public CambioEstadoSolicitud(LocalDateTime fechaHoraInicio, String estadoSolicitud, String estadoContenedor) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estadoSolicitud = estadoSolicitud;
        this.estadoContenedor = estadoContenedor;
    }

}