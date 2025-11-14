package puj.ads.proyectocatalogo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "libro")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Category category;

    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;

    @Column(name = "ano_publicacion")
    private Integer anioPublicacion;

    private String descripcion;

    @Column(name = "portada_url")
    private String portadaUrl;

    @Column(name = "cantidad_total")
    private Integer cantidadTotal;

    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible;

    private String idioma;
    private String tags;
}
