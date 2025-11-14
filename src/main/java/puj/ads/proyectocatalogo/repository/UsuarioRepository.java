package puj.ads.proyectocatalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puj.ads.proyectocatalogo.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
}
