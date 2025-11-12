package com.microservicio.transporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.transporte.entity.Transportista;

public interface TransportistaRepository extends JpaRepository<Transportista, String> {
    java.util.Optional<Transportista> findByDni(String dni);
    void deleteByDni(String dni);
    java.util.Optional<Transportista> findByEmail(String email);
}
