package puj.ads.proyectocatalogo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.dto.BookDTO;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.BookStatistics;
import puj.ads.proyectocatalogo.model.Category;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoryRepository;
import puj.ads.proyectocatalogo.repository.PrestamoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PrestamoRepository prestamoRepository;

    @InjectMocks
    private BookService bookService;

    private Category category;
    private BookDTO dto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(10L);
        category.setNombre("Programación");

        dto = new BookDTO();
        dto.setTitulo("Clean Code");
        dto.setAutor("Robert Martin");
        dto.setIsbn("9780132350884");
        dto.setCategoriaId(10L);
        dto.setCantidadTotal(5);
        dto.setCantidadDisponible(10); // se debe recortar a 5
    }

    @Test
    void addBook_deberiaNormalizarDisponibilidad_yPersistir() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book saved = bookService.addBook(dto);

        assertThat(saved.getTitulo()).isEqualTo("Clean Code");
        assertThat(saved.getCantidadDisponible()).isEqualTo(5);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isEqualTo(category);
    }

    @Test
    void addBook_conIsbnDuplicado_deberiaFallar() {
        when(bookRepository.existsByIsbn(dto.getIsbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.addBook(dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_conPrestamosActivos_lanzaExcepcion() {
        when(prestamoRepository.existsByLibroIdAndEstadoIdIn(eq(9L), anySet())).thenReturn(true);

        assertThatThrownBy(() -> bookService.deleteBook(9L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateAvailability_clampEntreCeroYTotal() {
        Book book = new Book();
        book.setId(7L);
        book.setCantidadTotal(3);
        book.setCantidadDisponible(1);
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        bookService.updateAvailability(7L, -5);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        assertThat(captor.getValue().getCantidadDisponible()).isZero();
    }

    @Test
    void getStatistics_retornaMetricas() {
        Book book = new Book();
        book.setId(8L);
        book.setCantidadTotal(4);
        book.setCantidadDisponible(2);
        when(bookRepository.findById(8L)).thenReturn(Optional.of(book));
        when(prestamoRepository.countActivos(8L)).thenReturn(3L);
        when(prestamoRepository.countVencidos(8L)).thenReturn(1L);

        BookStatistics stats = bookService.getStatistics(8L);

        assertThat(stats.getPrestamosActivos()).isEqualTo(3);
        assertThat(stats.getPrestamosVencidos()).isEqualTo(1);
        assertThat(stats.getCantidadDisponible()).isEqualTo(2);
    }

    @Test
    void search_devuelvePageEsperada() {
        Book book = new Book();
        book.setTitulo("Clean Code");
        when(bookRepository.search(any(), any(), any(), any())).thenReturn(new PageImpl<>(List.of(book)));

        var response = bookService.search("clean", null, true, "title", 0, 10);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getTitulo()).isEqualTo("Clean Code");
    }
}
