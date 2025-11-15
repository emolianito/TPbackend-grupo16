package com.microservicios.Solicitudes.dto.external;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositoDTO {
    //TODO: ARREGLAR POR QUE VA A DAR ERROR AL QUERER TRAER LA DIRECCION
    private int id;
    private String nombre;
    private String ubicacion;
    private float costoEstadiaDiaria;
}