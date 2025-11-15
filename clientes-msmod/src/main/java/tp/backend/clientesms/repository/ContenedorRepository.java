package tp.backend.clientesms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.backend.clientesms.entity.Contenedor;

public interface ContenedorRepository extends JpaRepository<Contenedor, Integer> {
  List<Contenedor> findByDepositoId(Long depositoId);
}
