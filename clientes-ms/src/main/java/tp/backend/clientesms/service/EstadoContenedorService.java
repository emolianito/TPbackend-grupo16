package tp.backend.clientesms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.backend.clientesms.dto.EstadoContenedorDTO;
import tp.backend.clientesms.entity.EstadoContenedor;
import tp.backend.clientesms.repository.EstadoContenedorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadoContenedorService {

    private final EstadoContenedorRepository repository;

    public List<EstadoContenedorDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EstadoContenedorDTO toDto(EstadoContenedor e) {
        if (e == null) return null;
        EstadoContenedorDTO dto = new EstadoContenedorDTO();
        // Ajusta los setters según los nombres reales en tu DTO/entidad
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        return dto;
    }
}