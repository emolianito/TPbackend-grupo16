package com.microservicios.Solicitudes.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "rutaAsignada")
    @JsonBackReference
    @EqualsAndHashCode.Exclude // ⬅️ CORRECCIÓN CRÍTICA
    @ToString.Exclude
    private Solicitud solicitud;

    private String tiempoEstimado;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude // ⬅️ BUENA PRÁCTICA (Colecciones)
    @ToString.Exclude
    @OrderBy("id ASC")
    private List<Tramo> tramos;
}
