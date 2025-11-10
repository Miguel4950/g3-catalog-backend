package puj.ads.proyectocatalogo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") 
public class BookController {

    private final BookService bookSvc;

    public BookController(BookService bookSvc) {
        this.bookSvc = bookSvc;
    }

    @GetMapping
    public PagedResult<Book> getAllBooks(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "category", required = false) String category, // G5 envía el Nombre (String)
            @RequestParam(name = "available", required = false) Boolean available,
            @RequestParam(name = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        return bookSvc.getAllBooks(search, category, available, sortBy, page, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable("id") int id) {
        Book b = bookSvc.getBookById(id);
        return (b == null) ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(b);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_BIBLIOTECARIO')") 
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        try {
            Book created = bookSvc.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_BIBLIOTECARIO')")
    public ResponseEntity<Book> updateBook(
            @PathVariable("id") int id,
            @RequestBody Book book
    ) {
        Book updated = bookSvc.updateBook(id, book);
        if (updated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) {
        bookSvc.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    // API INTERNA PARA G4 (PRÉSTAMOS).
    @PutMapping("/{id}/updateAvailability")
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados (G4) pueden llamar
    public ResponseEntity<Void> updateAvailability(
            @PathVariable("id") int id,
            @RequestParam("change") int change 
    ) {
        try {
            bookSvc.updateAvailability(id, change);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
