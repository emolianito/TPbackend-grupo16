package com.microservicios.Solicitudes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.microservicios.Solicitudes.entity.Ruta;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    @Query(value = "SELECT DISTINCT r.id, r.costo_estimado, r.tiempo_estimado, r.id_solicitud FROM ruta r WHERE r.id_solicitud = :idSolicitud", nativeQuery = true)
    List<Object[]> findRutasBySolicitudIdNative(@Param("idSolicitud") Integer idSolicitud);

    // Devuelve sólo las rutas globales (no asignadas a ninguna solicitud)
    List<Ruta> findBySolicitudIsNull();
}
