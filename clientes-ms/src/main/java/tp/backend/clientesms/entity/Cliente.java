package tp.backend.clientesms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data @AllArgsConstructor @NoArgsConstructor
public class Cliente {
    @Id
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

}
