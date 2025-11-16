package com.microservicios.Solicitudes.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicios.Solicitudes.dto.responses.EstadoContenedorDTO;
import com.microservicios.Solicitudes.dto.external.ContenedorDTO;
import org.springframework.core.ParameterizedTypeReference;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceClient {
    private final RestClient restClient;
    private static final String CLIENTE_SERVICE_URL = "http://localhost:8081";

    public ContenedorDTO obtenerContenedorPorId(Integer idContenedor) {
        String url = CLIENTE_SERVICE_URL + "/api/contenedores/" + idContenedor;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ContenedorDTO.class);
    }

    public void actualizarContenedor(ContenedorDTO contenedorDTO) {
        String url = CLIENTE_SERVICE_URL + "/api/contenedores/" + contenedorDTO.getId();
        restClient.put()
                .uri(url)
                .body(contenedorDTO)
                .retrieve()
                .body(Void.class);
    }

    public List<EstadoContenedorDTO> obtenerEstadosContendor() {
        String url = CLIENTE_SERVICE_URL + "/api/estados-contenedor";
        return restClient.get()
            .uri(url)
            .retrieve()
            .body(new ParameterizedTypeReference<List<EstadoContenedorDTO>>() {});
    }
}