package puj.ads.proyectocatalogo.controller;

import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.service.BookService;

/**
 * Controlador simplificado que opera completamente en memoria. Los métodos ya
 * no dependen de Spring MVC, pero mantienen firmas parecidas para facilitar
 * futuras migraciones hacia un framework web real.
 */
public class BookController {

    private final BookService bookSvc;

    public BookController(BookService bookSvc) {
        this.bookSvc = bookSvc;
    }

    public PagedResult<Book> getAllBooks(
            String search,
            String category,
            Boolean available,
            String sortBy,
            int page,
            int limit
    ) {
        return bookSvc.getAllBooks(search, category, available, sortBy, page, limit);
    }

    public Book getById(int id) {
        return bookSvc.getBookById(id);
    }

    public Book addBook(Book book) {
        return bookSvc.addBook(book);
    }

    public Book updateBook(int id, Book book) {
        return bookSvc.updateBook(id, book);
    }

    public void deleteBook(int id) {
        bookSvc.deleteBook(id);
    }

    public void updateAvailability(int id, int change) throws Exception {
        bookSvc.updateAvailability(id, change);
    }
}
