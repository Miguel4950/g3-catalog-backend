package puj.ads.proyectocatalogo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @Column(name = "id_usuario")
    private Integer id;
    private String username;
    private String nombre;
    @Column(name = "contrasena")
    private String password;
    @Column(name = "id_tipo_usuario")
    private Integer tipoUsuarioId;
    @Column(name = "id_estado_usuario")
    private Integer estadoUsuarioId;
    @Column(name = "intentos_fallidos")
    private int intentosFallidos;
}
