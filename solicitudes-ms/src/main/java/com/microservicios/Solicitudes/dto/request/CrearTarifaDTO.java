package com.microservicios.Solicitudes.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearTarifaDTO {
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;

    private Double costoBasePorKm;               // costo por km estimado
    private Double costoLitroCombustible;        // precio promedio del combustible
    private Double consumoPromedioCombustible;   // consumo combustible promedio
    private Double costoEstadiaDiariaDeposito;   // estadía estimada
    private Double costoFijoPorTramo;    
}
