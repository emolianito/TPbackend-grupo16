package tp.backend.clientesms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.backend.clientesms.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
}
