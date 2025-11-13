package com.microservicios.Solicitudes.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.microservicios.Solicitudes.entity.Tarifa;

public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {

    @Query("SELECT t FROM Tarifa t WHERE t.fechaInicioVigencia <= CURRENT_DATE AND (t.fechaFinVigencia IS NULL OR t.fechaFinVigencia >= CURRENT_DATE) ORDER BY t.fechaInicioVigencia DESC LIMIT 1")
    Tarifa getTarifaVigente();
    
    @Query("SELECT t FROM Tarifa t WHERE (t.fechaInicioVigencia <= ?1 AND (t.fechaFinVigencia IS NULL OR t.fechaFinVigencia >= ?1)) OR (t.fechaInicioVigencia <= ?2 AND (t.fechaFinVigencia IS NULL OR t.fechaFinVigencia >= ?2))")
    List<Tarifa> findTarifasSuperpuestas(LocalDate fechaInicio, LocalDate fechaFin);
}
