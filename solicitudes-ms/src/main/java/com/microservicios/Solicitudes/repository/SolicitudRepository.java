package com.microservicios.Solicitudes.repository;
import com.microservicios.Solicitudes.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
}
