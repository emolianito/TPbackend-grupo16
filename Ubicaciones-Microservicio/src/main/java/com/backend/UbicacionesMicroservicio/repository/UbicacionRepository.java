package com.backend.UbicacionesMicroservicio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.UbicacionesMicroservicio.entity.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

}
