package com.microservicio.transporte.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class crearCamionDto {

	private String patente;
	private String dniTransportista;
	private BigDecimal capacidadMaxPeso;
	private BigDecimal capacidadMaxVolumen;
	private BigDecimal consumoPorKm;
	private BigDecimal costoBasePorKm;

}