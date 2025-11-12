package tp.backend.clientesms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tp.backend.clientesms.entity.Cliente;
import tp.backend.clientesms.service.ClienteService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // POST /api/clientes
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        Cliente creado = clienteService.crearCliente(cliente);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + creado.getDni()))
                .body(creado);
    }

    // GET /api/clientes/{dni}
    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> obtenerPorDni(@PathVariable String dni) {
        Cliente c = clienteService.getClienteById(dni);
        return ResponseEntity.ok(c);
    }

    // GET /api/clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.getAll());
    }

    // PUT /api/clientes/{dni}
    @PutMapping("/{dni}")
    public ResponseEntity<Cliente> actualizar(@PathVariable String dni, @RequestBody Cliente cliente) {
        Cliente actualizado = clienteService.updateCliente(dni, cliente);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE /api/clientes/{dni}
    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminar(@PathVariable String dni) {
        clienteService.deleteCliente(dni);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}