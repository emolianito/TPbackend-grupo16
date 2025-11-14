package com.microservicios.Solicitudes.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambioEstadoSolicitud {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_solicitud")
    private Solicitud solicitud;

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