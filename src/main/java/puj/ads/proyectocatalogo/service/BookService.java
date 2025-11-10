package puj.ads.proyectocatalogo.service;

import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.Categoria;
import puj.ads.proyectocatalogo.model.PagedResult;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoriaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (page < 0) page = 0;
        if (limit <= 0) limit = 20;

        Integer categoryId = null;
        if (categoryName != null && !categoryName.isBlank()) {
            Optional<Categoria> cat = categoriaRepo.findByNombreIgnoreCase(categoryName);
            if (cat.isPresent()) {
                categoryId = cat.get().getId_categoria();
            } else {
                // No existe la categoría: devolvemos un resultado vacío.
                return buildEmptyResult(page, limit);
            }
        }

        List<Book> filtered = bookRepo.findBooksByCriteria(search, categoryId, available);

        Comparator<Book> comparator;
        if (sortBy == null) {
            sortBy = "title";
        }
        switch (sortBy.toLowerCase(Locale.ROOT)) {
            case "author":
                comparator = Comparator.comparing(b -> nullSafeLower(b.getAutor()));
                break;
            case "year":
                comparator = Comparator.comparing(Book::getAno_publicacion,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            default:
                comparator = Comparator.comparing(b -> nullSafeLower(b.getTitulo()));
                break;
        }

        List<Book> sorted = filtered.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int total = sorted.size();
        int totalPages = (int) Math.ceil(total / (double) limit);
        int fromIndex = Math.min(page * limit, total);
        int toIndex = Math.min(fromIndex + limit, total);
        List<Book> pageItems = sorted.subList(fromIndex, toIndex);

        PagedResult<Book> result = new PagedResult<>();
        result.items = pageItems;
        result.page = page;
        result.limit = limit;
        result.total = total;
        result.totalPages = totalPages;
        return result;
    }

    public Book addBook(Book book) {
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio");
        }
        return bookRepo.save(book);
    }

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

    public void deleteBook(int id) {
        bookRepo.deleteById(id);
    }

    // API para G4 (Préstamos)
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

    private static String nullSafeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private static PagedResult<Book> buildEmptyResult(int page, int limit) {
        PagedResult<Book> empty = new PagedResult<>();
        empty.items = List.of();
        empty.page = page;
        empty.limit = limit;
        empty.total = 0;
        empty.totalPages = 0;
        return empty;
    }
}
