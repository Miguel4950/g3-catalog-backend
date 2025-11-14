package puj.ads.proyectocatalogo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.dto.BookDTO;
import puj.ads.proyectocatalogo.dto.PagedResponse;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.BookStatistics;
import puj.ads.proyectocatalogo.model.Category;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoryRepository;
import puj.ads.proyectocatalogo.repository.PrestamoRepository;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final Pattern ISBN_PATTERN = Pattern.compile("^[0-9-]{10,17}$");
    private static final Set<Integer> ESTADOS_BLOQUEO = Set.of(1, 2, 4);

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PrestamoRepository prestamoRepository;

    public PagedResponse<Book> search(String search, Long categoryId, Boolean available,
                                      String sortBy, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), resolveSort(sortBy));
        Page<Book> result = bookRepository.search(normalize(search), categoryId, available, pageable);
        return PagedResponse.<Book>builder()
                .items(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .total(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
    }

    @Transactional
    public Book addBook(BookDTO dto) {
        validateIsbn(dto.getIsbn());
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN ya existe");
        }
        Book book = mapToEntity(new Book(), dto);
        book.setIsbn(dto.getIsbn());
        book.setCategory(resolveCategory(dto.getCategoriaId()));
        sanitizeAvailability(book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookDTO dto) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));

        if (dto.getIsbn() != null && !dto.getIsbn().isBlank() && !dto.getIsbn().equals(existing.getIsbn())) {
            validateIsbn(dto.getIsbn());
            if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ISBN ya existe");
            }
            existing.setIsbn(dto.getIsbn());
        }

        mapToEntity(existing, dto);

        if (dto.getCategoriaId() != null) {
            existing.setCategory(resolveCategory(dto.getCategoriaId()));
        }

        sanitizeAvailability(existing);
        return bookRepository.save(existing);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (prestamoRepository.existsByLibroIdAndEstadoIdIn(id, ESTADOS_BLOQUEO)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar porque el libro tiene préstamos activos");
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public void updateAvailability(Long id, int change) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        int nuevaDisponibilidad = Math.max(0,
                Math.min(book.getCantidadDisponible() + change, book.getCantidadTotal()));
        book.setCantidadDisponible(nuevaDisponibilidad);
        bookRepository.save(book);
    }

    public BookStatistics getStatistics(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        return BookStatistics.builder()
                .bookId(id)
                .prestamosActivos(prestamoRepository.countActivos(id))
                .prestamosVencidos(prestamoRepository.countVencidos(id))
                .cantidadTotal(book.getCantidadTotal())
                .cantidadDisponible(book.getCantidadDisponible())
                .build();
    }

    public List<Book> getPopular(int limit) {
        return bookRepository.findMostPopular(PageRequest.of(0, Math.max(1, limit))).getContent();
    }

    private Book mapToEntity(Book book, BookDTO dto) {
        if (dto.getTitulo() != null) book.setTitulo(dto.getTitulo());
        if (dto.getAutor() != null) book.setAutor(dto.getAutor());
        if (dto.getEditorial() != null) book.setEditorial(dto.getEditorial());
        if (dto.getDescripcion() != null) book.setDescripcion(dto.getDescripcion());
        if (dto.getPortadaUrl() != null) book.setPortadaUrl(dto.getPortadaUrl());
        if (dto.getIdioma() != null) book.setIdioma(dto.getIdioma());
        if (dto.getTags() != null) book.setTags(dto.getTags());
        if (dto.getCantidadTotal() != null) book.setCantidadTotal(dto.getCantidadTotal());
        if (dto.getCantidadDisponible() != null) book.setCantidadDisponible(dto.getCantidadDisponible());
        if (dto.getAnioPublicacion() != null) book.setAnioPublicacion(dto.getAnioPublicacion());
        return book;
    }

    private Category resolveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    private void validateIsbn(String isbn) {
        if (isbn == null || !ISBN_PATTERN.matcher(isbn).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN inválido");
        }
    }

    private void sanitizeAvailability(Book book) {
        int total = Math.max(0, book.getCantidadTotal() == null ? 0 : book.getCantidadTotal());
        int disponible = Math.max(0, Math.min(book.getCantidadDisponible() == null ? total : book.getCantidadDisponible(), total));
        book.setCantidadTotal(total);
        book.setCantidadDisponible(disponible);
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by("titulo").ascending();
        }
        return switch (sortBy.toLowerCase()) {
            case "author" -> Sort.by("autor").ascending();
            case "year" -> Sort.by("anioPublicacion").descending();
            case "popular" -> Sort.by(Sort.Direction.ASC, "cantidadDisponible");
            default -> Sort.by("titulo").ascending();
        };
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
