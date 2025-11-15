package com.microservicios.Solicitudes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- Imports Originales ---
import com.microservicios.Solicitudes.entity.Ruta;
import com.microservicios.Solicitudes.services.RutaService;
import lombok.RequiredArgsConstructor;

// --- !! IMPORTS NUEVOS (DE NUESTRO PLAN) !! ---
// ¡¡ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ PRESENTE!!
import com.microservicios.Solicitudes.dto.request.GenerarRutasRequestDTO; // <--- DTO Nuevo (Paso 1)
import com.microservicios.Solicitudes.dto.responses.RutaTentativaDTO; // <--- DTO Nuevo (Paso 1)
import jakarta.validation.Valid; // Para la validación
// --- FIN IMPORTS NUEVOS ---

@RestController
@RequestMapping("/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    // ---
    // INICIO: NUESTRO NUEVO ENDPOINT (PASO 6)
    // ---

    /**
     * [NUEVO] Consulta rutas tentativas dinámicas (RF 3).
     * Esta es la línea que fallaba (aprox. 45).
     */
    @PostMapping("/tentativas")
    public ResponseEntity<List<RutaTentativaDTO>> obtenerRutasTentativas(
            // ¡¡LA CLAVE ES USAR "GenerarRutasRequestDTO" AQUÍ!!
            @Valid @RequestBody GenerarRutasRequestDTO request) {

        // ¡Aquí llamamos al método Orquestador del PASO 5!
        // Como 'request' es del tipo correcto, esta llamada SÍ funcionará.
        List<RutaTentativaDTO> rutas = rutaService.generarRutasTentativas(request);

        if (rutas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rutas);
    }

    // ---
    // FIN: NUESTRO NUEVO ENDPOINT
    // ---

    // ---
    // INICIO: CÓDIGO ANTIGUO (MOCK)
    // (Lo dejamos intacto)
    // ---

    /*
     * [MOCK] Genera rutas sugeridas entre una ubicación origen y destino.
     
    @PostMapping("/sugerir")
    public ResponseEntity<List<Ruta>> generarRutas(@RequestBody GenerarRutasDTO dto) { // <--- Usa el DTO Antiguo
        System.out.println("ADVERTENCIA: Se está usando el endpoint MOCK /rutas/sugerir");

        // Llama al método MOCK (generarRutasSugeridas)
        List<Ruta> rutas = rutaService.generarRutasSugeridas(
                dto.getIdUbicacionOrigen(),
                dto.getIdUbicacionDestino());
        return ResponseEntity.ok(rutas);
    }
*/
    /**
     * [MOCK] Obtiene todas las rutas sugeridas globales (sin solicitud asignada).
     */
    @GetMapping
    public ResponseEntity<List<Ruta>> obtenerRutas() {
        System.out.println("ADVERTENCIA: Se está usando el endpoint MOCK GET /rutas");
        List<Ruta> rutas = rutaService.obtenerRutasSugeridas();
        return ResponseEntity.ok(rutas);
    }

    /**
     * [MOCK] Obtiene una ruta específica por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerRutaPorId(@PathVariable Integer id) {
        System.out.println("ADVERTENCIA: Se está usando el endpoint MOCK GET /rutas/{id}");
        Ruta ruta = rutaService.obtenerRutaPorId(id);
        return ResponseEntity.ok(ruta);
    }

    /**
     * [MOCK] Elimina una ruta por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Integer id) {
        System.out.println("ADVERTENCIA: Se está usando el endpoint MOCK DELETE /rutas/{id}");
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}