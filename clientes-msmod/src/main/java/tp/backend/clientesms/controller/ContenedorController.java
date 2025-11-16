package tp.backend.clientesms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tp.backend.clientesms.dto.ContenedorDTO;
import tp.backend.clientesms.service.ContenedorService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
@RequiredArgsConstructor
public class ContenedorController {

    private final ContenedorService service;

    @GetMapping
    public ResponseEntity<List<ContenedorDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenedorDTO> getById(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/deposito/{depositoId}")
    public ResponseEntity<List<ContenedorDTO>> getByDepositoId(@PathVariable Long depositoId) {
        List<ContenedorDTO> contenedores = service.findByDepositoId(depositoId);
        return ResponseEntity.ok(contenedores);
    }

    @PostMapping
    public ResponseEntity<ContenedorDTO> create(@Valid @RequestBody ContenedorDTO dto,
            UriComponentsBuilder uriBuilder) {
        ContenedorDTO saved = service.saveFromDto(dto);
        // Volver a cargar el DTO guardado para asegurar que las asociaciones anidadas
        // estén pobladas
        ContenedorDTO full = service.findById(saved.getId()).orElse(saved);
        URI uri = uriBuilder.path("/api/contenedores/{id}").buildAndExpand(full.getId()).toUri();
        return ResponseEntity.created(uri).body(full);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenedorDTO> update(@PathVariable Integer id, @RequestBody ContenedorDTO dto) {
        return service.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}