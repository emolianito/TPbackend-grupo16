package com.backend.UbicacionesMicroservicio.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContenedorDTO {
  private Long id;
  private Double peso;
  private Double volumen;
  private Long estadoId;
  private String clienteDni;
  private Long depositoId;
  private EstadoContenedorDTO estado;
  private ClienteDTO cliente;

  public ContenedorDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getPeso() {
    return peso;
  }

  public void setPeso(Double peso) {
    this.peso = peso;
  }

  public Double getVolumen() {
    return volumen;
  }

  public void setVolumen(Double volumen) {
    this.volumen = volumen;
  }

  public Long getEstadoId() {
    return estadoId;
  }

  public void setEstadoId(Long estadoId) {
    this.estadoId = estadoId;
  }

  public String getClienteDni() {
    return clienteDni;
  }

  public void setClienteDni(String clienteDni) {
    this.clienteDni = clienteDni;
  }

  public Long getDepositoId() {
    return depositoId;
  }

  public void setDepositoId(Long depositoId) {
    this.depositoId = depositoId;
  }

  public EstadoContenedorDTO getEstado() {
    return estado;
  }

  public void setEstado(EstadoContenedorDTO estado) {
    this.estado = estado;
  }

  public ClienteDTO getCliente() {
    return cliente;
  }

  public void setCliente(ClienteDTO cliente) {
    this.cliente = cliente;
  }
}