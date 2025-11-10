package puj.ads.proyectocatalogo.controller;

import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.Categoria;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoriaRepository;

import java.util.List;

/**
 * Controlador en memoria para las categorías. Su responsabilidad principal es
 * delegar en los repositorios, aplicando validaciones sencillas donde es
 * necesario.
 */
public class CategoryController {

    private final BookRepository bookRepo;
    private final CategoriaRepository categoriaRepo;

    public CategoryController(BookRepository bookRepo, CategoriaRepository categoriaRepo) {
        this.bookRepo = bookRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public List<Categoria> getAllCategories() {
        return categoriaRepo.findAll();
    }

    public List<Book> getBooksByCategoryId(int id) {
        return bookRepo.findByIdCategoria(id);
    }

    public Categoria createCategory(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        return categoriaRepo.save(categoria);
    }

    public Categoria updateCategory(int id, Categoria categoriaDetails) {
        Categoria cat = categoriaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        cat.setNombre(categoriaDetails.getNombre());
        cat.setDescripcion(categoriaDetails.getDescripcion());

        return categoriaRepo.save(cat);
    }
}
