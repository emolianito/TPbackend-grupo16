package tp.backend.clientesms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contenedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "peso", precision = 14, scale = 3)
    private BigDecimal peso;

    @Column(name = "volumen", precision = 14, scale = 3)
    private BigDecimal volumen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado", referencedColumnName = "id")
    private EstadoContenedor estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente", referencedColumnName = "dni")
    private Cliente cliente;

    @Column(name = "deposito_id")
    private Long depositoId;
}