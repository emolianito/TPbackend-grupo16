package com.microservicio.transporte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transportista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transportista {

	@Id
	@Column(name = "dni", length = 20)
	private String dni;

	@Column(name = "nombre", length = 120)
	private String nombre;

	@Column(name = "apellido", length = 120)
	private String apellido;

	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "telefono", length = 40)
	private String telefono;

}
