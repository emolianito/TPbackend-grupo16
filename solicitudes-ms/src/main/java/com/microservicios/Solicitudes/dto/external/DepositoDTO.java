package com.microservicios.Solicitudes.dto.external;

import lombok.Data;

@Data
public class DepositoDTO {

    private int id;

    private Double costoEstadiaDiaria;

    private String nombre;

    private UbicacionDTO ubicacion;
}