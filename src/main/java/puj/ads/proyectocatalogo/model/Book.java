package puj.ads.proyectocatalogo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "libro")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_libro;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    private String editorial;

    private String ano_publicacion; // G1 lo definió como VARCHAR

    @Column(nullable = false)
    private Integer id_categoria; // G1 usa INT

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String portada_url;

    @Column(nullable = false)
    private Integer cantidad_total;

    @Column(nullable = false)
    private Integer cantidad_disponible;
}
