package com.microservicios.Solicitudes.dto.external;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContenedorDTO {

    private Integer id;

    private BigDecimal peso;

    private BigDecimal volumen;

    private Integer estadoId;

    private String clienteDni;
    
    private Long depostioId;

}