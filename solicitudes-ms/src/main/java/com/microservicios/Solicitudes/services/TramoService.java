package com.microservicios.Solicitudes.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.microservicios.Solicitudes.client.TransporteServiceClient;

import com.microservicios.Solicitudes.entity.TipoTramo;
import com.microservicios.Solicitudes.entity.Tramo;
import com.microservicios.Solicitudes.repository.TramoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramoService {

    private final TramoRepository tramoRepository;
    private final SolicitudService solicitudService;
    private final CalculoCostoService calculoCostoService;
    private final TransporteServiceClient transporteServiceClient;

    public void asignarCamion(Integer idTramo, String patenteCamion) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        Integer idContenedor = tramo.getRuta().getSolicitud().getIdContenedor();

        if (transporteServiceClient.verificarCapacidad(patenteCamion, idContenedor)) {
            transporteServiceClient.ocuparCamion(patenteCamion);
            tramo.setPatenteCamion(patenteCamion);
            tramoRepository.save(tramo);
            return;
        }
        throw new RuntimeException("El camión no es adecuado para el contenedor del tramo");
    }

    public void iniciarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        if (tramo.getPatenteCamion() == null) {
            throw new RuntimeException("No se puede iniciar un tramo sin camión asignado");
        }
        TipoTramo tipoTramo = tramo.getTipoTramo();

        if (tipoTramo.equals(TipoTramo.ORIGEN_DEPOSITO) || tipoTramo.equals(TipoTramo.ORIGEN_DESTINO)
                || tipoTramo.equals(TipoTramo.DEPOSITO_DEPOSITO)) {
            solicitudService.cambiarEstadoSolicitud(tramo.getRuta().getSolicitud(), "EN_RUTA", "EN_CAMINO");

        }

        // Guardar el tramo actualizado

        tramo.setFechaInicio(LocalDateTime.now());
        tramoRepository.save(tramo);
        // Intentar calcular el costo real del tramo anterios ya que ahora tiene los
        // dias de estadia
        try {
            if (tipoTramo.equals(TipoTramo.DEPOSITO_DEPOSITO) || tipoTramo.equals(TipoTramo.DEPOSITO_DESTINO)) {
                Tramo tramoAnterior = tramoRepository.findById(tramo.getId() - 1)
                        .orElseThrow(() -> new RuntimeException("Tramo anterior no encontrado"));
                calculoCostoService.calcularCostoRealTramo(tramoAnterior, tramo);
            } else {
                if (tipoTramo.equals(TipoTramo.ORIGEN_DESTINO)) {
                    calculoCostoService.calcularCostoRealTramo(tramo, null);
                }
            }

        } catch (Exception e) {
            // Si falla el cálculo, loguear pero NO fallar
            System.err.println("Error calculando costo real del tramo " + idTramo + ": " + e.getMessage());
            // Usar el costo aproximado como fallback
            tramo.setCostoReal(0.0);
        }

    }

    public Tramo finalizarTramo(Integer idTramo) {
        // Obtener el tramo y validar que existe
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));

        // Validar que el tramo no esté ya finalizado
        if (tramo.getFechaFin() != null) {
            throw new RuntimeException("El tramo ya está finalizado");
        }

        // Validar que el tramo haya sido iniciado
        if (tramo.getFechaInicio() == null) {
            throw new RuntimeException("No se puede finalizar un tramo que no ha sido iniciado");
        }

        // Validar que tenga un camión asignado
        if (tramo.getPatenteCamion() == null) {
            throw new RuntimeException("No se puede finalizar un tramo sin camión asignado");
        }

        // Establecer la fecha de finalización
        tramo.setFechaFin(LocalDateTime.now());

        TipoTramo tipoTramo = tramo.getTipoTramo();

        if (tipoTramo.equals(TipoTramo.DEPOSITO_DESTINO) || tipoTramo.equals(TipoTramo.ORIGEN_DESTINO)) {
            solicitudService.finalizarSolicitud(tramo.getRuta().getSolicitud().getId());
            transporteServiceClient.liberarCamion(tramo.getPatenteCamion());
        } else if (tipoTramo.equals(TipoTramo.ORIGEN_DEPOSITO) || tipoTramo.equals(TipoTramo.DEPOSITO_DEPOSITO)) {
            solicitudService.cambiarEstadoSolicitud(tramo.getRuta().getSolicitud(), "EN_DEPOSITO", "EN_DEPOSITO");

        }
        tramoRepository.save(tramo);
        return tramo;
    }

    public void eliminarTramo(Integer idTramo) {
        Tramo tramo = tramoRepository.findById(idTramo)
                .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        tramoRepository.delete(tramo);
    }
}
