package puj.ads.proyectocatalogo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "libro") // Nombre de la tabla de G1
@Data // Lombok para Getters/Setters
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_libro; // Coincide con G1

    @Column(nullable = false, unique = true)
    private String isbn; // Coincide con G1

    @Column(nullable = false)
    private String titulo; // Coincide con G1

    @Column(nullable = false)
    private String autor; // Coincide con G1

    private String editorial; // Coincide con G1

    private String ano_publicacion; // Coincide con G1 (VARCHAR)

    @Column(nullable = false)
    private Integer id_categoria; // Coincide con G1

    @Column(columnDefinition = "TEXT")
    private String descripcion; // Coincide con G1

    private String portada_url; // Coincide con G1

    @Column(nullable = false)
    private Integer cantidad_total; // Coincide con G1

    @Column(nullable = false)
    private Integer cantidad_disponible; // Coincide con G1

    // Se omiten 'tags', 'idioma', 'prestamosCount' de G3 porque no están en la BD de G1
}