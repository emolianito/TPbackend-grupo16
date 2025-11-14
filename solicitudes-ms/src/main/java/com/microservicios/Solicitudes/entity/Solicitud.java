package com.microservicios.Solicitudes.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ruta", referencedColumnName = "id")
    @JsonManagedReference
    private Ruta rutaAsignada;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoSolicitud estado;

    @Column(nullable = true)
    private LocalDate fechaSolicitud;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private List<CambioEstadoSolicitud> cambiosEstado = new ArrayList<>();

    
    public void addCambioEstado(CambioEstadoSolicitud cambioEstado) {
        cambioEstado.setSolicitud(this);
        this.cambiosEstado.add(cambioEstado);
    }
}
