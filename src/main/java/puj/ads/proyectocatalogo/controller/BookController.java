package puj.ads.proyectocatalogo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import puj.ads.proyectocatalogo.dto.BookDTO;
import puj.ads.proyectocatalogo.dto.PagedResponse;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.BookStatistics;
import puj.ads.proyectocatalogo.service.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public PagedResponse<Book> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookService.search(search, categoryId, available, sortBy, page, size);
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Integer id) {
        return bookService.getBook(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public Book addBook(@Valid @RequestBody BookDTO dto) {
        return bookService.addBook(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public Book updateBook(@PathVariable Integer id, @RequestBody BookDTO dto) {
        return bookService.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }

    @PutMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public void updateAvailability(@PathVariable Integer id, @RequestParam int change) {
        bookService.updateAvailability(id, change);
    }

    @GetMapping("/{id}/stats")
    public BookStatistics stats(@PathVariable Integer id) {
        return bookService.getStatistics(id);
    }

    @GetMapping("/popular")
    public java.util.List<Book> popular(@RequestParam(defaultValue = "10") int limit) {
        return bookService.getPopular(limit);
    }
}
