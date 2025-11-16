package com.backend.UbicacionesMicroservicio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeocodingService {

  @Value("${google.maps.apikey}")
  private String apiKey;

  private final RestTemplate restTemplate;

  public GeocodingService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Coordenadas obtenerCoordenadas(String direccion) throws Exception {
    String url = String.format(
        "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
        direccion.replace(" ", "+"), apiKey);

    JsonNode response = restTemplate.getForObject(url, JsonNode.class);

    if (response == null || !"OK".equals(response.get("status").asText())) {
      throw new Exception("No se pudo geocodificar la dirección");
    }

    JsonNode location = response.get("results").get(0).get("geometry").get("location");

    return new Coordenadas(
        location.get("lat").floatValue(),
        location.get("lng").floatValue());
  }

  public static class Coordenadas {
    private float latitud;
    private float longitud;

    public Coordenadas(float latitud, float longitud) {
      this.latitud = latitud;
      this.longitud = longitud;
    }

    public float getLatitud() {
      return latitud;
    }

    public float getLongitud() {
      return longitud;
    }
  }
}