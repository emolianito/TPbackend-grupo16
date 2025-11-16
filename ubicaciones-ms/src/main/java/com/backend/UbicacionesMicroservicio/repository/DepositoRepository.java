package com.backend.UbicacionesMicroservicio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.UbicacionesMicroservicio.entity.Deposito;

public interface DepositoRepository extends JpaRepository<Deposito, Integer> {
}
