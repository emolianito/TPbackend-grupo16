package com.microservicios.Solicitudes.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicios.Solicitudes.dto.external.CamionDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransporteServiceClient {
    private final RestClient restClient;
    private static final String TRANSPORTE_SERVICE_URL = "http://localhost:8082";

    public CamionDTO obtenerCamionPorId(String patente) {
        String url = TRANSPORTE_SERVICE_URL + "/camiones/" + patente;
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(CamionDTO.class);
    }
    public void ocuparCamion(String patente) {
        String url = TRANSPORTE_SERVICE_URL + "/camiones/" + patente + "/ocupar";
        restClient.patch()
                .uri(url)
                .retrieve()
                .toBodilessEntity(); 

    }
}