package puj.ads.proyectocatalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import puj.ads.proyectocatalogo.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNombreIgnoreCase(String nombre);
}
