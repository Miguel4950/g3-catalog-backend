package puj.ads.proyectocatalogo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.Categoria;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoriaRepository;

import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepo;
    private final CategoriaRepository categoriaRepo;

    public BookService(BookRepository bookRepo, CategoriaRepository categoriaRepo) {
        this.bookRepo = bookRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public Book getBookById(int id) {
        return bookRepo.findById(id).orElse(null);
    }

    public PagedResult<Book> getAllBooks(String search, String categoryName, Boolean available,
                                         String sortBy, int page, int limit) {
        
        Integer categoryId = null;
        if (categoryName != null && !categoryName.isBlank()) {
            Optional<Categoria> cat = categoriaRepo.findByNombreIgnoreCase(categoryName);
            if (cat.isPresent()) {
                categoryId = cat.get().getId_categoria();
            } else {
                categoryId = -1; 
            }
        }

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
        Page<Book> bookPage = bookRepo.findBooksByCriteria(search, categoryId, available, pageable);

        PagedResult<Book> result = new PagedResult<>();
        result.items = bookPage.getContent();
        result.page = bookPage.getNumber();
        result.limit = bookPage.getSize();
        result.total = bookPage.getTotalElements();
        result.totalPages = bookPage.getTotalPages();
        return result;
    }

    @Transactional
    public Book addBook(Book book) {
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio");
        }
        return bookRepo.save(book);
    }

    @Transactional
    public Book updateBook(int id, Book bookDetails) {
        Book existing = getBookById(id);
        if (existing == null) return null;

        existing.setTitulo(bookDetails.getTitulo());
        existing.setAutor(bookDetails.getAutor());
        existing.setEditorial(bookDetails.getEditorial());
        existing.setAno_publicacion(bookDetails.getAno_publicacion());
        existing.setId_categoria(bookDetails.getId_categoria());
        existing.setDescripcion(bookDetails.getDescripcion());
        existing.setCantidad_total(bookDetails.getCantidad_total());
        existing.setCantidad_disponible(bookDetails.getCantidad_disponible());

        return bookRepo.save(existing);
    }

    @Transactional
    public void deleteBook(int id) {
        bookRepo.deleteById(id);
    }

    // API para G4 (Préstamos)
    @Transactional
    public void updateAvailability(int id, int change) throws Exception {
        Book b = getBookById(id);
        if (b == null) {
            throw new Exception("Libro no encontrado (ID: " + id + ")");
        }
        
        int nueva = b.getCantidad_disponible() + change;
        
        if (nueva < 0) {
            throw new Exception("No hay disponibilidad suficiente para prestar (Stock: " + b.getCantidad_disponible() + ")");
        }
        if (nueva > b.getCantidad_total()) {
             nueva = b.getCantidad_total(); 
        }
        
        b.setCantidad_disponible(nueva);
        bookRepo.save(b);
    }
}
