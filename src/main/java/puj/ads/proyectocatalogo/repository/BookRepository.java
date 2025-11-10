package puj.ads.proyectocatalogo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import puj.ads.proyectocatalogo.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    // Esta es la consulta que implementa toda la lógica de filtrado de G3
    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR b.titulo LIKE %:search% OR b.autor LIKE %:search% OR b.isbn LIKE %:search%) " +
           "AND (:category IS NULL OR b.id_categoria = :category) " +
           "AND (:available IS NULL OR " +
           "     (:available = TRUE AND b.cantidad_disponible > 0) OR " +
           "     (:available = FALSE AND b.cantidad_disponible = 0))")
    Page<Book> findBooksByCriteria(
            @Param("search") String search,
            @Param("category") Integer category, // G1 usa ID de categoría (INT)
            @Param("available") Boolean available,
            Pageable pageable
    );

    // Consulta para G4 (tu grupo)
    Book findById(int id);
}