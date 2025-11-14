package com.backend.UbicacionesMicroservicio.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import com.backend.UbicacionesMicroservicio.dto.ContenedorDTO;

@Service
public class ContenedorClientService {

  private final RestTemplate restTemplate;
  private final String contenedoresBaseUrl;

  public ContenedorClientService(RestTemplate restTemplate,
      @Value("${microservicio.contenedores.url}") String contenedoresBaseUrl) {
    this.restTemplate = restTemplate;
    this.contenedoresBaseUrl = contenedoresBaseUrl;
  }

  public List<ContenedorDTO> getContenedoresByDeposito(int id) {
    String url = contenedoresBaseUrl + "/deposito/{depositoId}";
    try {
      ResponseEntity<ContenedorDTO[]> response = restTemplate.getForEntity(
          url,
          ContenedorDTO[].class,
          id);
      ContenedorDTO[] body = response.getBody();
      return body != null ? Arrays.asList(body) : Collections.emptyList();
    } catch (HttpStatusCodeException ex) {
      System.err.println("Error al obtener contenedores: " + ex.getStatusCode());
      return Collections.emptyList();
    }
  }
}