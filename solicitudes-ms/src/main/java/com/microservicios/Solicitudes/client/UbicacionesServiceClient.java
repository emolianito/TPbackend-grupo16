package com.microservicios.Solicitudes.client;

import com.microservicios.Solicitudes.dto.external.CoordenadasDTO;
import com.microservicios.Solicitudes.dto.external.DepositoDTO;
import com.microservicios.Solicitudes.dto.external.DistanciaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class UbicacionesServiceClient {

    private final RestClient restClient;

    public UbicacionesServiceClient(RestClient.Builder builder,
            @Value("${microservicios.ubicaciones.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Llama al endpoint GET /api/distancia
     */
    public DistanciaDTO obtenerDistancia(String origenCoords, String destinoCoords) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/distancia")
                        .queryParam("origen", origenCoords)
                        .queryParam("destino", destinoCoords)
                        .build())
                .retrieve()
                .body(DistanciaDTO.class);
    }

    /**
     * Llama al endpoint GET /ubicaciones/depositos
     */
    public List<DepositoDTO> obtenerDepositos() {
        return restClient.get()
                .uri("/ubicaciones/depositos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<DepositoDTO>>() {
                });
    }

    // --- ¡¡NUEVO MÉTODO AÑADIDO!! ---
    /**
     * Llama al endpoint GET /ubicaciones/depositos/{id}
     * Necesario para CalculoCostoService.
     */
    public DepositoDTO obtenerDepositoPorId(Integer id) {
        // Llama a: http://localhost:8082/ubicaciones/depositos/{id}
        return restClient.get()
                .uri("/ubicaciones/depositos/{id}", id) // Pasamos el 'id' como variable de ruta
                .retrieve()
                .body(DepositoDTO.class); // Espera un solo DepositoDTO
    }

    public CoordenadasDTO obtenerCoordenadas(String direccion) {
        // Llama a:
        // http://localhost:8082/ubicaciones/localizaciones/geocode?direccion=...
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ubicaciones/localizaciones/geocode")
                        .queryParam("direccion", direccion)
                        .build())
                .retrieve()
                .body(CoordenadasDTO.class);
    }
}