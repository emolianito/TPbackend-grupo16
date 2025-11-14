package com.microservicios.Solicitudes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microservicios.Solicitudes.entity.EstadoSolicitud;

import java.util.Optional;

public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Integer> {
    Optional<EstadoSolicitud> findByNombre(String nombre);
}