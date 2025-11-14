package puj.ads.proyectocatalogo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import puj.ads.proyectocatalogo.model.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("""
        SELECT b FROM Book b
        WHERE (:search IS NULL OR LOWER(b.titulo) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.autor) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:categoryId IS NULL OR b.category.id = :categoryId)
          AND (:available IS NULL OR (:available = true AND b.cantidadDisponible > 0)
                               OR (:available = false AND b.cantidadDisponible = 0))
        """)
    Page<Book> search(@Param("search") String search,
                      @Param("categoryId") Integer categoryId,
                      @Param("available") Boolean available,
                      Pageable pageable);

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b ORDER BY (b.cantidadTotal - b.cantidadDisponible) DESC, b.titulo ASC")
    Page<Book> findMostPopular(Pageable pageable);
}
