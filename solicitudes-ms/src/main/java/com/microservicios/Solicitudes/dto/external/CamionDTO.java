package com.microservicios.Solicitudes.dto.external;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class CamionDTO {
    
	private Long id;
	private String patente;
	private String dniTransportista;
	private BigDecimal capacidadMaxPeso;
	private BigDecimal capacidadMaxVolumen;
	private Boolean estaDisponible;
	private BigDecimal consumoPorKm;
	private BigDecimal costoBasePorKm;

}
