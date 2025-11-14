package com.microservicio.transporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.transporte.entity.Camion;

public interface CamionRepository extends JpaRepository<Camion, Long> {
	java.util.Optional<Camion> findByPatente(String patente);
	void deleteByPatente(String patente);
	java.util.List<Camion> findByEstaDisponibleTrue();
	java.util.List<Camion> findByDniTransportista(String dniTransportista);
}
