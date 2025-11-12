package com.backend.UbicacionesMicroservicio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.UbicacionesMicroservicio.dto.DepositoRequest;
import com.backend.UbicacionesMicroservicio.entity.Deposito;
import com.backend.UbicacionesMicroservicio.entity.Ubicacion;
import com.backend.UbicacionesMicroservicio.repository.DepositoRepository;

@Service
public class DepositoService {
  private final DepositoRepository depositoRepository;
  private final UbicacionService ubicacionService;
  private final GeocodingService geocodingService;

  public DepositoService(DepositoRepository depositoRepository,
      UbicacionService ubicacionService,
      GeocodingService geocodingService) {
    this.depositoRepository = depositoRepository;
    this.ubicacionService = ubicacionService;
    this.geocodingService = geocodingService;
  }

  public Deposito createDeposito(DepositoRequest request) {
    try {
      // 1. Geocodificar la dirección
      String direccionLimpia = request.getDireccion().trim().replaceAll("\\s+", " ");
      GeocodingService.Coordenadas coords = geocodingService.obtenerCoordenadas(direccionLimpia);

      // 2. Crear la ubicación
      Ubicacion ubicacion = new Ubicacion();
      ubicacion.setDireccion(direccionLimpia);
      ubicacion.setLatitud(coords.getLatitud());
      ubicacion.setLongitud(coords.getLongitud());

      // 3. Guardar la ubicación
      Ubicacion ubicacionGuardada = ubicacionService.createUbicacion(ubicacion);

      // 4. Crear el depósito
      Deposito deposito = new Deposito();
      deposito.setNombre(request.getNombre());
      deposito.setUbicacion(ubicacionGuardada);
      deposito.setCostoDiaEstadia(request.getCostoDiaEstadia()); // NUEVO

      // 5. Guardar el depósito
      return depositoRepository.save(deposito);

    } catch (Exception e) {
      throw new RuntimeException("Error al crear depósito: " + e.getMessage());
    }
  }

  public Deposito getDepositoById(int id) {
    return depositoRepository.findById(id).orElse(null);
  }

  public List<Deposito> getAllDepositos() {
    return depositoRepository.findAll();
  }

  public Deposito updateDeposito(int id, DepositoRequest request) {
    Deposito deposito = getDepositoById(id);
    if (deposito != null) {
      try {
        // Actualizar nombre y costo
        deposito.setNombre(request.getNombre());
        deposito.setCostoDiaEstadia(request.getCostoDiaEstadia()); // NUEVO

        // Si cambió la dirección, geocodificar nuevamente
        String direccionLimpia = request.getDireccion().trim().replaceAll("\\s+", " ");
        if (!deposito.getUbicacion().getDireccion().equals(direccionLimpia)) {
          GeocodingService.Coordenadas coords = geocodingService.obtenerCoordenadas(direccionLimpia);

          Ubicacion ubicacion = deposito.getUbicacion();
          ubicacion.setDireccion(direccionLimpia);
          ubicacion.setLatitud(coords.getLatitud());
          ubicacion.setLongitud(coords.getLongitud());
        }

        return depositoRepository.save(deposito);
      } catch (Exception e) {
        throw new RuntimeException("Error al actualizar depósito: " + e.getMessage());
      }
    }
    return null;
  }

  public boolean deleteDeposito(int id) {
    if (depositoRepository.existsById(id)) {
      depositoRepository.deleteById(id);
      return true;
    }
    return false;
  }
}