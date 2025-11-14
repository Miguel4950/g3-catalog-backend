package puj.ads.proyectocatalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    @NotBlank
    private String isbn;

    @NotNull
    private Integer categoriaId;

    private String editorial;
    private String descripcion;
    private String portadaUrl;
    private String idioma;
    private String tags;

    @Min(0)
    private Integer cantidadTotal;

    @Min(0)
    private Integer cantidadDisponible;

    private Integer anioPublicacion;
}
