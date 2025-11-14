// java
package tp.backend.clientesms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenedorDTO {
    private Integer id;

    @NotNull(message = "peso es requerido")
    @Positive(message = "peso debe ser mayor que 0")
    private BigDecimal peso;

    @NotNull(message = "volumen es requerido")
    @Positive(message = "volumen debe ser mayor que 0")
    private BigDecimal volumen;

    @NotNull(message = "estadoId es requerido")
    private Integer estadoId;

    @NotNull(message = "clienteDni es requerido")
    private String clienteDni;

    // Campos anidados para respuesta
    private EstadoContenedorDTO estado;
    private ClienteDTO cliente;
}
