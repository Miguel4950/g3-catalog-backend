package puj.ads.proyectocatalogo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Long id;

    @Column(name = "id_usuario")
    private Integer usuarioId;

    @Column(name = "id_libro")
    private Integer libroId;

    @Column(name = "id_estado_prestamo")
    private Integer estadoId;

    private LocalDateTime fecha_prestamo;
    private LocalDateTime fecha_devolucion_esperada;
    private LocalDateTime fecha_devolucion_real;
}
