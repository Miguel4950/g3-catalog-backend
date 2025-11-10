package puj.ads.proyectocatalogo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // Permite llamadas de G5 (Frontend)
public class BookController {

    private final BookService bookSvc;

    public BookController(BookService bookSvc) {
        this.bookSvc = bookSvc;
    }

    /**
     * GET /api/books
     * Pasa los filtros al servicio, que los ejecutará en la BD.
     */
    @GetMapping
    public PagedResult<Book> getAllBooks(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "category", required = false) Integer category, // G1 usa INT
            @RequestParam(name = "available", required = false) Boolean available,
            @RequestParam(name = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        // La lógica de filtrado ahora está en el BookService
        return bookSvc.getAllBooks(search, category, available, sortBy, page, limit);
    }

    // GET /api/books/{id} -> 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable("id") int id) { // G1 usa INT
        Book b = bookSvc.getBookById(id);
        return (b == null) ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(b);
    }

    // POST /api/books (rol: bibliotecario – simulado)
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        // Aquí faltaría la seguridad real de G2
        try {
            Book created = bookSvc.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // PUT /api/books/{id} (rol: bibliotecario – simulado)
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable("id") int id, // G1 usa INT
            @RequestBody Book book
    ) {
        Book updated = bookSvc.updateBook(id, book);
        if (updated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado");
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/books/{id} (rol: admin – simulado)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) { // G1 usa INT
        bookSvc.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    // --- API INTERNA PARA G4 (PRÉSTAMOS) ---
    // Esta API no la consume el frontend, la consumes TÚ (G4)
    
    @PutMapping("/{id}/updateAvailability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable("id") int id,
            @RequestParam("change") int change // ej. -1 (prestar) o +1 (devolver)
    ) {
        try {
            bookSvc.updateAvailability(id, change);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Si G4 intenta prestar un libro sin stock, G3 devuelve 400
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // (Se eliminan los endpoints /popular y /availability, G5 los puede calcular
    // o se pueden recrear si es necesario)
}