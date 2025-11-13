// java
package com.microservicio.transporte.dto.external;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContenedorDto {

    private Integer id;

    private BigDecimal peso;

    private BigDecimal volumen;

    private Integer estadoId;

    private String clienteDni;

}