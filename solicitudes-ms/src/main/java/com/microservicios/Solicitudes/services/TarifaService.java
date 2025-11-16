package com.microservicios.Solicitudes.services;

import java.time.LocalDate;
import java.util.List;

import com.microservicios.Solicitudes.entity.Tarifa;
import com.microservicios.Solicitudes.repository.TarifaRepository;
import org.springframework.stereotype.Service;

@Service
public class TarifaService {
    
    private final TarifaRepository tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public Tarifa obtenerTarifa(Integer id) {
        return tarifaRepository.findById(id).orElse(null);
    }

    public List<Tarifa> obtenerTodasLasTarifas() {
        return tarifaRepository.findAll();
    }

    public Tarifa crearTarifa(Tarifa tarifa) {
        // Validar que la fecha de inicio no sea nula
        if (tarifa.getFechaInicioVigencia() == null) {
            throw new IllegalArgumentException("La fecha de inicio de vigencia no puede ser nula");
        }

        // Buscar tarifas que se superpongan con el nuevo período
        List<Tarifa> tarifasSuperpuestas = tarifaRepository.findTarifasSuperpuestas(
            tarifa.getFechaInicioVigencia(),
            tarifa.getFechaFinVigencia() != null ? tarifa.getFechaFinVigencia() : LocalDate.of(9999, 12, 31)
        );

        // Si hay tarifas superpuestas, cerrar su vigencia
        if (!tarifasSuperpuestas.isEmpty()) {
            for (Tarifa existente : tarifasSuperpuestas) {
                existente.setFechaFinVigencia(tarifa.getFechaInicioVigencia().minusDays(1));
                tarifaRepository.save(existente);
            }
        }

        // Guardar la nueva tarifa
        return tarifaRepository.save(tarifa);
    }

    public Tarifa getTarifaVigente() {
        return tarifaRepository.getTarifaVigente();
    }
}
