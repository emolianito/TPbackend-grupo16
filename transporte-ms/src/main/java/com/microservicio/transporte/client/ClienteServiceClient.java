package com.microservicio.transporte.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicio.transporte.dto.external.ContenedorDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceClient {
    private final RestClient restClient;
    private static final String CLIENTE_SERVICE_URL = "http://localhost:8081";

    public ContenedorDto obtenerContenedorPorId(Integer idContenedor) {
        String url = CLIENTE_SERVICE_URL + "/api/contenedores/" + idContenedor;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ContenedorDto.class);
    }
  
}