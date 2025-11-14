package com.microservicio.transporte.dto.responses;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionDto {

	private Long id;
	private String patente;
	private String dniTransportista;
	private BigDecimal capacidadMaxPeso;
	private BigDecimal capacidadMaxVolumen;
	private Boolean estaDisponible;
	private BigDecimal consumoPorKm;
	private BigDecimal costoBasePorKm;

}
