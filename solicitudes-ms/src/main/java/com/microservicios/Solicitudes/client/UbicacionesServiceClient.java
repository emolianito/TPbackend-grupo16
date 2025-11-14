package com.microservicios.Solicitudes.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicios.Solicitudes.dto.external.DepositoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UbicacionesServiceClient {
     private final RestClient restClient;

    public DepositoDTO obtenerDepositoPorId(Integer depositoId) {
        return restClient.get()
                .uri("http://localhost:8092/depositos/" + depositoId) // cambiar después
                .retrieve()
                .body(DepositoDTO.class);
    }
}
