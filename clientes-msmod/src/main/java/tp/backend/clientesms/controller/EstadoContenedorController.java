package tp.backend.clientesms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tp.backend.clientesms.dto.EstadoContenedorDTO;
import tp.backend.clientesms.service.EstadoContenedorService;

import java.util.List;

@RestController
@RequestMapping("/api/estados-contenedor")
@RequiredArgsConstructor
public class EstadoContenedorController {

    private final EstadoContenedorService estadoContenedorService;

    @GetMapping
    public ResponseEntity<List<EstadoContenedorDTO>> listarTodos() {
        return ResponseEntity.ok(estadoContenedorService.listarTodos());
    }
}