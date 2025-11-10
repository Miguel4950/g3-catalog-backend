package puj.ads.proyectocatalogo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repo;

    // Se quita CsvLoader, ahora se inyecta el repositorio JPA
    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    // Se quita el método init() y loadInitial(). G1 se encarga de los datos.

    // -------------------- LECTURA (Conectado a G1) --------------------
    
    public Book getBookById(int id) {
        return repo.findById(id);
    }

    // Método principal que usa el Controller (con paginación y filtros)
    public PagedResult<Book> getAllBooks(String search, Integer category, Boolean available,
                                         String sortBy, int page, int limit) {
        
        // Mapeo de SortBy
        Sort sort;
        switch (sortBy.toLowerCase()) {
            case "author":
                sort = Sort.by("autor");
                break;
            case "year":
                sort = Sort.by("ano_publicacion").descending();
                break;
            default:
                sort = Sort.by("titulo");
                break;
        }

        Pageable pageable = PageRequest.of(page, limit, sort);
        
        // Llama a la consulta JPA
        Page<Book> bookPage = repo.findBooksByCriteria(search, category, available, pageable);

        // Convierte el Page<Book> de Spring al PagedResult que G3 definió
        PagedResult<Book> result = new PagedResult<>();
        result.items = bookPage.getContent();
        result.page = bookPage.getNumber();
        result.limit = bookPage.getSize();
        result.total = bookPage.getTotalElements();
        result.totalPages = bookPage.getTotalPages();
        return result;
    }

    // -------------------- ESCRITURA (Conectado a G1) --------------------
    
    @Transactional
    public Book addBook(Book book) {
        // Validaciones simples
        if (book.getTitulo() == null || book.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio");
        }
        return repo.save(book);
    }

    @Transactional
    public Book updateBook(int id, Book bookDetails) {
        Book existing = getBookById(id);
        if (existing == null) return null; // O lanzar Not Found

        // Actualiza los campos (se omite la lógica de G3)
        existing.setTitulo(bookDetails.getTitulo());
        existing.setAutor(bookDetails.getAutor());
        existing.setEditorial(bookDetails.getEditorial());
        existing.setAnio_publicacion(bookDetails.getAno_publicacion());
        existing.setId_categoria(bookDetails.getId_categoria());
        existing.setDescripcion(bookDetails.getDescripcion());
        existing.setCantidad_total(bookDetails.getCantidad_total());
        existing.setCantidad_disponible(bookDetails.getCantidad_disponible());

        return repo.save(existing);
    }

    @Transactional
    public void deleteBook(int id) {
        repo.deleteById(id);
    }

    // -------------------- INTEGRACIÓN CON PRÉSTAMOS (GRUPO 4) --------------------
    
    /**
     * API para que G4 (Préstamos) actualice la disponibilidad.
     * Esta API es usada por G4 cuando un libro se presta (-1) o se devuelve (+1).
     */
    @Transactional
    public void updateAvailability(int id, int change) throws Exception {
        Book b = getBookById(id);
        if (b == null) {
            throw new Exception("Libro no encontrado para actualizar disponibilidad");
        }
        
        int nueva = b.getCantidad_disponible() + change;
        
        // Validación de G1 (no puede ser negativo, no puede superar el total)
        if (nueva < 0) {
            throw new Exception("No hay disponibilidad suficiente para prestar");
        }
        if (nueva > b.getCantidad_total()) {
             nueva = b.getCantidad_total(); // Si devuelven más, se capea al total
        }
        
        b.setCantidad_disponible(nueva);
        repo.save(b);
    }
}