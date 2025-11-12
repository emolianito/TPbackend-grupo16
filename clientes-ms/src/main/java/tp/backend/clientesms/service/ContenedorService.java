package tp.backend.clientesms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tp.backend.clientesms.dto.ClienteDTO;
import tp.backend.clientesms.dto.ContenedorDTO;
import tp.backend.clientesms.dto.EstadoContenedorDTO;
import tp.backend.clientesms.entity.Contenedor;
import tp.backend.clientesms.entity.EstadoContenedor;
import tp.backend.clientesms.entity.Cliente;
import tp.backend.clientesms.repository.ContenedorRepository;
import tp.backend.clientesms.repository.EstadoContenedorRepository;
import tp.backend.clientesms.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContenedorService {

    private final ContenedorRepository repository;
    private final EstadoContenedorRepository estadoRepo;
    private final ClienteRepository clienteRepo;

    public List<ContenedorDTO> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<ContenedorDTO> findById(Integer id) {
        return repository.findById(id).map(this::toDto);
    }

    public ContenedorDTO saveFromDto(ContenedorDTO dto) {
        // Verificar existencia de estado
        EstadoContenedor estado = estadoRepo.findById(dto.getEstadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no existe"));

        // Verificar existencia de cliente
        Cliente cliente = clienteRepo.findById(dto.getClienteDni())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no existe"));

        // Mapear y guardar
        Contenedor c = new Contenedor();
        c.setPeso(dto.getPeso());
        c.setVolumen(dto.getVolumen());
        c.setEstado(estado);
        c.setCliente(cliente);

        Contenedor saved = repository.save(c);

        // Mapear a DTO (puedes reutilizar tu toDto)
        ContenedorDTO result = toDto(saved);
        return result;
    }

    public Optional<ContenedorDTO> update(Integer id, ContenedorDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setPeso(dto.getPeso());
            existing.setVolumen(dto.getVolumen());

            if (dto.getEstadoId() != null) {
                estadoRepo.findById(dto.getEstadoId()).ifPresent(existing::setEstado);
            } else {
                existing.setEstado(null);
            }

            if (dto.getClienteDni() != null) {
                clienteRepo.findById(dto.getClienteDni()).ifPresent(existing::setCliente);
            } else {
                existing.setCliente(null);
            }

            return toDto(repository.save(existing));
        });
    }

    public boolean delete(Integer id) {
        return repository.findById(id).map(c -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    // Mapear entidad a DTO incluyendo objetos anidados
    private ContenedorDTO toDto(Contenedor c) {
        if (c == null) return null;

        ContenedorDTO dto = ContenedorDTO.builder()
                .id(c.getId())
                .peso(c.getPeso())
                .volumen(c.getVolumen())
                .estadoId(c.getEstado() != null ? c.getEstado().getId() : null)
                .clienteDni(c.getCliente() != null ? c.getCliente().getDni() : null)
                .build();

        EstadoContenedor e = c.getEstado();
        if (e != null) {
            dto.setEstado(new EstadoContenedorDTO(e.getId(), e.getNombre()));
        }

        Cliente cl = c.getCliente();
        if (cl != null) {
            dto.setCliente(new ClienteDTO(cl.getDni(), cl.getNombre(), cl.getApellido(), cl.getEmail(), cl.getTelefono()));
        }

        return dto;
    }
}