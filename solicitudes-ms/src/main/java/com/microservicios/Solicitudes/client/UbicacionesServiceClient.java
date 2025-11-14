package com.microservicios.Solicitudes.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicios.Solicitudes.dto.external.DepositoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UbicacionesServiceClient {
     private final RestClient restClient;

     private static final String UBICACIONES_SERVICE_URL = "http://localhost:8092";

    public DepositoDTO obtenerDepositoPorId(Integer depositoId) {
        return restClient.get()
                .uri(UBICACIONES_SERVICE_URL + "/depositos/" + depositoId)
                .retrieve()
                .body(DepositoDTO.class);
    }
}
