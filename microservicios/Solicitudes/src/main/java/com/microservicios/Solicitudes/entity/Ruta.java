package com.microservicios.Solicitudes.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_solicitud")
    @JsonBackReference
    private Solicitud solicitud;

    private Double costoEstimado;
    private String tiempoEstimado;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Tramo> tramos;

}
