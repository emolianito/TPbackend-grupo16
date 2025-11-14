package tp.backend.clientesms.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoContenedorDTO {

    private Integer id;
    private String nombre;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public EstadoContenedorDTO(Integer id) {
        this.id = id;
    }
}
