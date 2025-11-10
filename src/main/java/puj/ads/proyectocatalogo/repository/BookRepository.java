package puj.ads.proyectocatalogo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import puj.ads.proyectocatalogo.model.Book;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR b.titulo LIKE %:search% OR b.autor LIKE %:search% OR b.isbn LIKE %:search%) " +
           "AND (:categoryId IS NULL OR b.id_categoria = :categoryId) " +
           "AND (:available IS NULL OR " +
           "     (:available = TRUE AND b.cantidad_disponible > 0) OR " +
           "     (:available = FALSE AND b.cantidad_disponible = 0))")
    Page<Book> findBooksByCriteria(
            @Param("search") String search,
            @Param("categoryId") Integer categoryId,
            @Param("available") Boolean available,
            Pageable pageable
    );
    
    List<Book> findByIdCategoria(Integer idCategoria);
}
