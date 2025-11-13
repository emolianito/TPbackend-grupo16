package com.microservicio.transporte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "camion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "patente", length = 15, nullable = false, unique = true)
	private String patente;

	@Column(name = "dni_transportista", length = 20, nullable = false)
	private String dniTransportista;

	@Column(name = "capacidad_max_peso", precision = 14, scale = 2)
	private BigDecimal capacidadMaxPeso;

	@Column(name = "capacidad_max_volumen", precision = 14, scale = 2)
	private BigDecimal capacidadMaxVolumen;

	@Column(name = "estaDisponible")
	private Boolean estaDisponible;

	@Column(name = "consumo_por_km", precision = 10, scale = 3)
	private BigDecimal consumoPorKm;

	@Column(name = "costo_base_por_km", precision = 12, scale = 2)
	private BigDecimal costoBasePorKm;

}
