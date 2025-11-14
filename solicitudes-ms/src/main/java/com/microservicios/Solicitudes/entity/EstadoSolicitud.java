package com.microservicios.Solicitudes.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estado_solicitud")
@Data
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}