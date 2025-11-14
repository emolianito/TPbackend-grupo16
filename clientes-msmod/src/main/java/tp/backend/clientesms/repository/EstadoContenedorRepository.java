// src/main/java/tp/backend/clientesms/repository/EstadoContenedorRepository.java
package tp.backend.clientesms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp.backend.clientesms.entity.EstadoContenedor;

import java.util.Optional;

@Repository
public interface EstadoContenedorRepository extends JpaRepository<EstadoContenedor, Integer> {
    Optional<EstadoContenedor> findByNombre(String nombre);
}