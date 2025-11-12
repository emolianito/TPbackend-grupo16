package tp.backend.clientesms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_contenedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoContenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 80, nullable = false)
    private String nombre;
}