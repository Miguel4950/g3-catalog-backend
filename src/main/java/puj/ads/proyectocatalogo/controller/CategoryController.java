package puj.ads.proyectocatalogo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.model.Categoria;
import puj.ads.proyectocatalogo.repository.BookRepository;
import puj.ads.proyectocatalogo.repository.CategoriaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final BookRepository bookRepo;
    private final CategoriaRepository categoriaRepo;

    public CategoryController(BookRepository bookRepo, CategoriaRepository categoriaRepo) {
        this.bookRepo = bookRepo;
        this.categoriaRepo = categoriaRepo;
    }

    @GetMapping
    public List<Categoria> getAllCategories() {
        return categoriaRepo.findAll();
    }

    // Busca libros por ID de categoría (INT)
    @GetMapping("/{id}/books")
    public List<Book> getBooksByCategoryId(@PathVariable("id") int id) {
        // CORRECCIÓN AQUÍ: findByIdCategoria -> findById_categoria
        return bookRepo.findById_categoria(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Categoria> createCategory(@RequestBody Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }
        Categoria newCat = categoriaRepo.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Categoria> updateCategory(
            @PathVariable("id") int id,
            @RequestBody Categoria categoriaDetails
    ) {
        Categoria cat = categoriaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        
        cat.setNombre(categoriaDetails.getNombre());
        cat.setDescripcion(categoriaDetails.getDescripcion());
        
        Categoria updatedCat = categoriaRepo.save(cat);
        return ResponseEntity.ok(updatedCat);
    }
}
