package tp.backend.clientesms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.backend.clientesms.entity.Cliente;
import tp.backend.clientesms.repository.ClienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Crear un cliente nuevo
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Obtener cliente por DNI
    public Cliente getClienteById(String dni) {
        return clienteRepository.findById(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con DNI: " + dni));
    }

    // Listar todos los clientes
    public List<Cliente> getAll() {
        return clienteRepository.findAll();
    }

    // Actualizar cliente
    public Cliente updateCliente(String dni, Cliente clienteActualizado) {
        Cliente existente = getClienteById(dni);

        existente.setNombre(clienteActualizado.getNombre());
        existente.setApellido(clienteActualizado.getApellido());
        existente.setEmail(clienteActualizado.getEmail());
        existente.setTelefono(clienteActualizado.getTelefono());

        return clienteRepository.save(existente);
    }

    // Eliminar cliente
    public void deleteCliente(String dni) {
        if (!clienteRepository.existsById(dni)) {
            throw new RuntimeException("Cliente no encontrado con DNI: " + dni);
        }
        clienteRepository.deleteById(dni);
    }
}