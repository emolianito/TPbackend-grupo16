package com.backend.UbicacionesMicroservicio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ContenedorClientService {

  private final RestTemplate restTemplate;

  @Value("${microservicio.contenedores.url}")
  private String contenedoresUrl;

  public ContenedorClientService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Object> obtenerContenedoresPorDeposito(int depositoId) {
    String url = contenedoresUrl + "/deposito/" + depositoId;
    Object[] contenedores = restTemplate.getForObject(url, Object[].class);
    return contenedores != null ? Arrays.asList(contenedores) : List.of();
  }
}