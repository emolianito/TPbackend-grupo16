package com.microservicios.Solicitudes.client;

import com.microservicios.Solicitudes.dto.external.CoordenadasDTO;
import com.microservicios.Solicitudes.dto.external.DepositoDTO;
import com.microservicios.Solicitudes.dto.external.DistanciaDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionesServiceClient {

    // DEFINIMOS LA URL BASE COMO CONSTANTE, replicando la estructura de TransporteServiceClient.
    // Usamos el puerto 8082, ya que parece ser el estándar en su proyecto.
    private static final String UBICACIONES_SERVICE_URL = "http://localhost:8082";

    private final RestClient restClient;

    /**
     * Llama al endpoint GET /api/distancia
     */
    public DistanciaDTO obtenerDistancia(String origenCoords, String destinoCoords) {
        // Construimos la URL completa con los query parameters.
        String url = String.format("%s/api/distancia?origen=%s&destino=%s", 
                                   UBICACIONES_SERVICE_URL, origenCoords, destinoCoords);
        
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(DistanciaDTO.class);
    }

    /**
     * Llama al endpoint GET /ubicaciones/depositos
     */
    public List<DepositoDTO> obtenerDepositos() {
        String url = UBICACIONES_SERVICE_URL + "/ubicaciones/depositos";
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DepositoDTO>>() {
                });
    }

    /**
     * Llama al endpoint GET /ubicaciones/depositos/{id}
     * Necesario para CalculoCostoService.
     */
    public DepositoDTO obtenerDepositoPorId(Integer id) {
        // Construimos la URL completa con la variable de ruta (path variable).
        String url = String.format("%s/ubicaciones/depositos/%d", UBICACIONES_SERVICE_URL, id);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(DepositoDTO.class);
    }

    public CoordenadasDTO obtenerCoordenadas(String direccion) {
        // Construimos la URL completa con el query parameter.
        String url = String.format("%s/ubicaciones/localizaciones/geocode?direccion=%s", 
                                   UBICACIONES_SERVICE_URL, direccion);
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(CoordenadasDTO.class);
    }
}