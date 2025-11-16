package com.microservicios.Solicitudes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.entity.Tramo;


public interface TramoRepository extends JpaRepository<Tramo, Integer> {

    List<Tramo> findByRuta(Ruta ruta);
}
