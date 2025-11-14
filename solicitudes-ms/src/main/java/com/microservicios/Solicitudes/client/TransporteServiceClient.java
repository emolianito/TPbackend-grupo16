package com.microservicios.Solicitudes.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.microservicios.Solicitudes.dto.external.CamionDTO;

import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransporteServiceClient {
    private final RestClient restClient;
    //TODO: DEFINIR BIEN LOS PUERTOS A USAR
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

    public void liberarCamion(String patente) {
        String url = TRANSPORTE_SERVICE_URL + "/camiones/" + patente + "/liberar";
        restClient.patch()
                .uri(url)
                .retrieve()
                .toBodilessEntity(); 
    }

    public boolean verificarCapacidad(String patenteCamion, Integer idContenedor) {
        String url = TRANSPORTE_SERVICE_URL + "/camiones/" + patenteCamion + "/verificar-capacidad/";
        Boolean resultado = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(idContenedor)
                .retrieve()
                .body(Boolean.class);
        return resultado != null && resultado;
    }
}