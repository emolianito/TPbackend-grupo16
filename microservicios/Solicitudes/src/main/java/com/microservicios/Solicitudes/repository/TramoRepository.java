package com.microservicios.Solicitudes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicios.Solicitudes.entity.Tramo;

public interface TramoRepository extends JpaRepository<Tramo, Integer> {

    
}
