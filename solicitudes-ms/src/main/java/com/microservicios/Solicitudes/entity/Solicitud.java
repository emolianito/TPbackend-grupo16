package com.microservicios.Solicitudes.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode; // ⬅️ NUEVO IMPORT
import lombok.ToString;


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
    @EqualsAndHashCode.Exclude // ⬅️ EXCLUIR PARA ROMPER EL CICLO
    @ToString.Exclude
    private Ruta rutaAsignada;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private EstadoSolicitud estado;

    @Column(nullable = true)
    private LocalDate fechaSolicitud;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude // ⬅️ EXCLUIR COLECCIONES
    @ToString.Exclude
    private List<CambioEstadoSolicitud> cambiosEstado = new ArrayList<>();

    
    public void addCambioEstado(CambioEstadoSolicitud cambioEstado) {
        this.cambiosEstado.add(cambioEstado);
    }
}