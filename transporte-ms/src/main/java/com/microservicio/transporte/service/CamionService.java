package com.microservicio.transporte.service;

import org.springframework.stereotype.Service;

import com.microservicio.transporte.dto.request.crearCamionDto;
import com.microservicio.transporte.dto.responses.CamionDto;
import com.microservicio.transporte.repository.CamionRepository;

import lombok.AllArgsConstructor;

import com.microservicio.transporte.entity.Camion;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class CamionService {
    private final CamionRepository camionRepository;
    private final TransportistaService transportistaService;

    public CamionDto crearCamion(crearCamionDto camion) {
        Camion entity = new Camion();
        entity.setPatente(camion.getPatente());
        entity.setTransportista(transportistaService.getTransportistaEntity(camion.getDniTransportista()));
        entity.setCapacidadMaxPeso(camion.getCapacidadMaxPeso());
        entity.setCapacidadMaxVolumen(camion.getCapacidadMaxVolumen());
        entity.setEstaDisponible(true);
        entity.setConsumoPorKm(camion.getConsumoPorKm());
        entity.setCostoBasePorKm(camion.getCostoBasePorKm());

        Camion saved = camionRepository.save(entity);
        return toDto(saved);
    }

    public List<CamionDto> listarCamiones() {
        return camionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CamionDto buscarPorPatente(String patente) {
        return camionRepository.findByPatente(patente)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camión no encontrado"));
    }

    public void eliminarPorPatente(String patente) {
        if (camionRepository.findByPatente(patente).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camión no encontrado");
        }
        camionRepository.deleteByPatente(patente);
    }

    public List<CamionDto> listarDisponibles() {
        return camionRepository.findByEstaDisponibleTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CamionDto ocuparCamion(String patente) {
        Camion camion = camionRepository.findByPatente(patente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camión no encontrado"));

        if (!camion.getEstaDisponible()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camión ya está ocupado");
        }

        camion.setEstaDisponible(false);
        Camion updated = camionRepository.save(camion);
        return toDto(updated);
    }

    public CamionDto liberarCamion(String patente) {
        Camion camion = camionRepository.findByPatente(patente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camión no encontrado"));

        if (camion.getEstaDisponible()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camión ya está libre");
        }

        camion.setEstaDisponible(true);
        Camion updated = camionRepository.save(camion);
        return toDto(updated);
    }

    private CamionDto toDto(Camion c) {
        CamionDto dto = new CamionDto();
    
        dto.setPatente(c.getPatente());
        dto.setDniTransportista(c.getTransportista().getDni());
        dto.setCapacidadMaxPeso(c.getCapacidadMaxPeso());
        dto.setCapacidadMaxVolumen(c.getCapacidadMaxVolumen());
        dto.setEstaDisponible(c.getEstaDisponible());
        dto.setConsumoPorKm(c.getConsumoPorKm());
        dto.setCostoBasePorKm(c.getCostoBasePorKm());
        return dto;
    }
}
