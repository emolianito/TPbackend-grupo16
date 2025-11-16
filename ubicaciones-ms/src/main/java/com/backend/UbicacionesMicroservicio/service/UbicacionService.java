package com.backend.UbicacionesMicroservicio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.UbicacionesMicroservicio.entity.Ubicacion;
import com.backend.UbicacionesMicroservicio.repository.UbicacionRepository;

@Service
public class UbicacionService {
  private final UbicacionRepository ubicacionRepository;

  public UbicacionService(UbicacionRepository ubicacionRepository) {
    this.ubicacionRepository = ubicacionRepository;
  }

  public Ubicacion createUbicacion(Ubicacion ubicacion) {
    return ubicacionRepository.save(ubicacion);
  }

  public Ubicacion getUbicacionById(int id) {
    return ubicacionRepository.findById(id).orElse(null);
  }

  public List<Ubicacion> getAllUbicacion() {
    return ubicacionRepository.findAll();
  }

  public Ubicacion updateUbicacion(int id, Ubicacion ubicacion) {
    Ubicacion existing = getUbicacionById(id);
    if (existing != null) {
      existing.setDireccion(ubicacion.getDireccion());
      existing.setLatitud(ubicacion.getLatitud());
      existing.setLongitud(ubicacion.getLongitud());
      return ubicacionRepository.save(existing);
    }
    return null;
  }

}
