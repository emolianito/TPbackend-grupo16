package com.microservicios.Solicitudes.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;



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
    @JsonBackReference
    @EqualsAndHashCode.Exclude 
    @ToString.Exclude // ⬅️ RESTAURADO Y EXCLUIDO
    private Solicitud solicitud; // ⬅️ CAMPO RESTAURADO

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estadoSolicitud; // vemos como sale
    private String estadoContenedor;

    public CambioEstadoSolicitud(LocalDateTime fechaHoraInicio, String estadoSolicitud, String estadoContenedor, Solicitud solicitud) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estadoSolicitud = estadoSolicitud;
        this.estadoContenedor = estadoContenedor;
        this.solicitud = solicitud;
    }

}