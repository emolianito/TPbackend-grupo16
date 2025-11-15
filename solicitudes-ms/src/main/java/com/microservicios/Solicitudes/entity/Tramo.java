package com.microservicios.Solicitudes.entity;

import java.time.LocalDate;

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

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Integer idUbicacionOrigen;
    private Integer idUbicacionDestino;
    private String patenteCamion;


    private TipoTramo tipoTramo;

    private Double costoAproximado;
    private Double costoReal;
    private String tiempo;
    

    private int idDepositoDestino;



    public Double getDistanciaKm() {
        // Lógica para calcular o retornar los kilómetros del tramo
        return 0.0;
    }
}
