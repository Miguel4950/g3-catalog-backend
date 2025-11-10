package puj.ads.proyectocatalogo.repository;

import puj.ads.proyectocatalogo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para los libros. Sustituye a Spring Data JPA en este
 * entorno sin dependencias externas.
 */
public class BookRepository {

    private final Map<Integer, Book> storage = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    public synchronized Optional<Book> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    public synchronized Book save(Book book) {
        if (book.getId_libro() == null) {
            book.setId_libro(sequence.getAndIncrement());
        }
        // Guardamos una copia defensiva para evitar efectos secundarios.
        Book copy = copyOf(book);
        storage.put(copy.getId_libro(), copy);
        return copyOf(copy);
    }

    public synchronized void deleteById(int id) {
        storage.remove(id);
    }

    public synchronized List<Book> findAll() {
        return storage.values().stream()
                .map(BookRepository::copyOf)
                .collect(Collectors.toList());
    }

    public synchronized List<Book> findByIdCategoria(Integer idCategoria) {
        return storage.values().stream()
                .filter(book -> idCategoria == null || idCategoria.equals(book.getId_categoria()))
                .map(BookRepository::copyOf)
                .collect(Collectors.toList());
    }

    public synchronized List<Book> findBooksByCriteria(String search, Integer categoryId, Boolean available) {
        String normalizedSearch = search == null ? null : search.toLowerCase(Locale.ROOT);
        List<Book> filtered = new ArrayList<>();
        for (Book book : storage.values()) {
            if (!matchesCategory(book, categoryId)) continue;
            if (!matchesAvailability(book, available)) continue;
            if (!matchesSearch(book, normalizedSearch)) continue;
            filtered.add(copyOf(book));
        }
        return filtered;
    }

    private static boolean matchesCategory(Book book, Integer categoryId) {
        return categoryId == null || categoryId.equals(book.getId_categoria());
    }

    private static boolean matchesAvailability(Book book, Boolean available) {
        if (available == null) {
            return true;
        }
        if (Boolean.TRUE.equals(available)) {
            return book.getCantidad_disponible() != null && book.getCantidad_disponible() > 0;
        }
        return book.getCantidad_disponible() != null && book.getCantidad_disponible() == 0;
    }

    private static boolean matchesSearch(Book book, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        return containsIgnoreCase(book.getTitulo(), search)
                || containsIgnoreCase(book.getAutor(), search)
                || containsIgnoreCase(book.getIsbn(), search);
    }

    private static boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private static Book copyOf(Book book) {
        Book copy = new Book();
        copy.setId_libro(book.getId_libro());
        copy.setIsbn(book.getIsbn());
        copy.setTitulo(book.getTitulo());
        copy.setAutor(book.getAutor());
        copy.setEditorial(book.getEditorial());
        copy.setAno_publicacion(book.getAno_publicacion());
        copy.setId_categoria(book.getId_categoria());
        copy.setDescripcion(book.getDescripcion());
        copy.setPortada_url(book.getPortada_url());
        copy.setCantidad_total(book.getCantidad_total());
        copy.setCantidad_disponible(book.getCantidad_disponible());
        return copy;
    }
}
