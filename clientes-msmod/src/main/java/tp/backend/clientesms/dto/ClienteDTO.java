package tp.backend.clientesms.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ClienteDTO(String dni) {
        this.dni = dni;
    }
}