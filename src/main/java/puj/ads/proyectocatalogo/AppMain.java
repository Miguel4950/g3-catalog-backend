package puj.ads.proyectocatalogo;

import puj.ads.proyectocatalogo.controller.BookController;
import puj.ads.proyectocatalogo.controller.CategoryController;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.Categoria;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoriaRepository;
import puj.ads.proyectocatalogo.service.BookService;

/**
 * Punto de entrada minimalista utilizado únicamente para demostrar que el
 * dominio puede inicializarse sin dependencias externas. No levanta un
 * servidor HTTP real, pero deja lista la infraestructura en memoria.
 */
public class AppMain {

    public static void main(String[] args) {
        CategoriaRepository categoriaRepository = new CategoriaRepository();
        BookRepository bookRepository = new BookRepository();
        BookService bookService = new BookService(bookRepository, categoriaRepository);

        // Registramos algunas categorías y libros de ejemplo para que el
        // resto de la aplicación pueda ser ejercitado manualmente si se desea.
        Categoria programacion = new Categoria();
        programacion.setNombre("Programación");
        programacion.setDescripcion("Libros relacionados con el desarrollo de software.");
        categoriaRepository.save(programacion);

        Book cleanCode = new Book();
        cleanCode.setTitulo("Clean Code");
        cleanCode.setAutor("Robert C. Martin");
        cleanCode.setIsbn("9780132350884");
        cleanCode.setEditorial("Prentice Hall");
        cleanCode.setAno_publicacion("2008");
        cleanCode.setId_categoria(programacion.getId_categoria());
        cleanCode.setDescripcion("Una guía para escribir código mantenible.");
        cleanCode.setCantidad_total(5);
        cleanCode.setCantidad_disponible(5);
        bookRepository.save(cleanCode);

        BookController bookController = new BookController(bookService);
        CategoryController categoryController = new CategoryController(bookRepository, categoriaRepository);

        System.out.println("Catálogo inicializado con " +
                categoryController.getAllCategories().size() + " categorías y " +
                bookController.getAllBooks(null, null, null, "title", 0, 10).items.size() +
                " libro(s).");
    }
}
