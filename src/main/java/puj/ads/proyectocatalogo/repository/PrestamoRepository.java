package puj.ads.proyectocatalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import puj.ads.proyectocatalogo.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.libroId = :bookId AND p.estadoId = 2")
    long countActivos(@Param("bookId") Integer bookId);

    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.libroId = :bookId AND p.estadoId = 4")
    long countVencidos(@Param("bookId") Integer bookId);

    boolean existsByLibroIdAndEstadoIdIn(Integer bookId, Iterable<Integer> estados);
}
