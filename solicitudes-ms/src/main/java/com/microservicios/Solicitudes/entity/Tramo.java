package com.microservicios.Solicitudes.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idRuta")
    @JsonBackReference
    private Ruta ruta;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    
    private LocalDateTime fechaHoraInicioEstimada;
    private LocalDateTime fechaHoraFinEstimada;


    private Integer idUbicacionOrigen;
    private Integer idUbicacionDestino;
    private String patenteCamion;


    private TipoTramo tipoTramo;

    private Double costoReal;
    private String tiempo;
    

    private int idDepositoDestino;

    private Double distanciaKm;
}
