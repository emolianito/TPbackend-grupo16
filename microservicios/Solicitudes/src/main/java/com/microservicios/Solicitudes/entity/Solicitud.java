package com.microservicios.Solicitudes.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "solicitudes")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idContenedor;
    private Integer idCliente;

    @Column(nullable = true)
    private Double costoEstimado;

    @Column(nullable = true)
    private Double costoReal;

    @Column(nullable = true)
    private String tiempoEstimado;

    @Column(nullable = true)
    private String tiempoReal;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Ruta rutaAsignada;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Column(nullable = true)
    private LocalDate fechaSolicitud;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private List<CambioEstadoSolicitud> cambiosEstado;

    
    public void addCambioEstado(CambioEstadoSolicitud cambioEstado) {
        this.cambiosEstado.add(cambioEstado);
    }
}
