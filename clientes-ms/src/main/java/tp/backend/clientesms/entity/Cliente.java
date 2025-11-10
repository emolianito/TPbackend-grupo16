package tp.backend.clientesms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Cliente {
    @Id
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

}
