package com.microservicio.transporte.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.microservicio.transporte.dto.request.crearTransportistaDto;
import com.microservicio.transporte.dto.responses.TransportistaDto;
import com.microservicio.transporte.entity.Transportista;
import com.microservicio.transporte.repository.TransportistaRepository;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public TransportistaService(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public TransportistaDto crearTransportista(crearTransportistaDto dto) {
        Transportista t = new Transportista();
        t.setDni(dto.getDni());
        t.setNombre(dto.getNombre());
        t.setApellido(dto.getApellido());
        t.setEmail(dto.getEmail());
        t.setTelefono(dto.getTelefono());

        Transportista saved = transportistaRepository.save(t);
        return toDto(saved);
    }

    public List<TransportistaDto> listarTransportistas() {
        return transportistaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public TransportistaDto buscarPorDni(String dni) {
        return transportistaRepository.findByDni(dni)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transportista no encontrado"));
    }

    public void eliminarPorDni(String dni) {
        if (transportistaRepository.findByDni(dni).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transportista no encontrado");
        }
        transportistaRepository.deleteByDni(dni);
    }

    private TransportistaDto toDto(Transportista t) {
        TransportistaDto out = new TransportistaDto();
        out.setDni(t.getDni());
        out.setNombre(t.getNombre());
        out.setApellido(t.getApellido());
        out.setEmail(t.getEmail());
        out.setTelefono(t.getTelefono());
        return out;
    }
    public Transportista getTransportistaEntity(String dni) {
        return transportistaRepository.findByDni(dni)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transportista no encontrado"));
    }
}
