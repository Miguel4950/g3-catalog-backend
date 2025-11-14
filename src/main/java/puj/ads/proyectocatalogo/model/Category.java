package puj.ads.proyectocatalogo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    private String nombre;

    private String descripcion;

    @Column(name = "esta_activa")
    private Boolean activa;
}
