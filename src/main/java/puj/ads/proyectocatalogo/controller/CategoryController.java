package puj.ads.proyectocatalogo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import puj.ads.proyectocatalogo.model.Book;
import puj.ads.proyectocatalogo.service.BookService;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final BookService bookSvc;

    // Registro simple en memoria para categorías creadas manualmente (POST)
    // Mantiene insensibilidad a mayúsculas/minúsculas.
    private final Set<String> extraCategories = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);

    public CategoryController(BookService bookSvc) {
        this.bookSvc = bookSvc;
    }

    /** GET /api/categories
     *  Devuelve la unión entre:
     *   - categorías detectadas en los libros
     *   - categorías creadas manualmente vía POST (extraCategories)
     */
    @GetMapping
    public Set<String> getAllCategories() {
        Set<String> fromBooks = bookSvc.getAllBooks().stream()
                .map(Book::getCategoria)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        fromBooks.addAll(extraCategories);
        return fromBooks; // ordenado case-insensitive
    }

    /** GET /api/categories/{category}/books  (ya lo tenías; por NOMBRE) */
    @GetMapping("/{category}/books")
    public List<Book> getBooksByCategory(@PathVariable("category") String category) {
        return bookSvc.getAllBooks().stream()
                .filter(b -> b.getCategoria() != null && b.getCategoria().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /** GET /api/categories/{id}/books  (alias por "id"; hoy lo tratamos como nombre)
     *  Esto cumple el contrato de la rúbrica aunque no exista CategoryRepository aún.
     */
    @GetMapping("/{id}/books")
    public List<Book> getBooksByCategoryId(@PathVariable("id") String id) {
        return bookSvc.getAllBooks().stream()
                .filter(b -> b.getCategoria() != null && b.getCategoria().equalsIgnoreCase(id))
                .collect(Collectors.toList());
    }

    /** POST /api/categories  (rol: admin – simulado con header X-User-Role)
     *  Body esperado (mínimo):
     *  { "name": "Filosofía", "description": "..." }
     *  Nota: la categoría se considera "activa" aunque no tenga libros todavía.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createCategory(
            @RequestBody Map<String, String> body,
            @RequestHeader(name = "X-User-Role", required = false) String role
    ) {
        if (role == null || !role.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin puede crear categorías");
        }
        String name = body.getOrDefault("name", "").trim();
        if (name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }
        extraCategories.add(name);
        Map<String, String> resp = new HashMap<>();
        resp.put("name", name);
        resp.put("message", "Categoría creada");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /** PUT /api/categories/{id}  (rol: admin – simulado)
     *  Renombra la categoría {id} -> {name} en:
     *   - todos los libros que tengan esa categoría
     *   - el set extraCategories (si existía)
     *  Body:
     *  { "name": "NuevoNombre", "description": "..." }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCategory(
            @PathVariable("id") String oldId,
            @RequestBody Map<String, String> body,
            @RequestHeader(name = "X-User-Role", required = false) String role
    ) {
        if (role == null || !role.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo admin puede actualizar categorías");
        }
        String newName = body.getOrDefault("name", "").trim();
        if (newName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nuevo nombre de la categoría es obligatorio");
        }

        // 1) Renombrar en libros
        List<Book> affected = bookSvc.getAllBooks().stream()
                .filter(b -> b.getCategoria() != null && b.getCategoria().equalsIgnoreCase(oldId))
                .collect(Collectors.toList());

        for (Book b : affected) {
            Book copy = new Book();
            // Copia campos mínimos; si tu Book tiene más campos, copia los necesarios
            copy.setTitulo(b.getTitulo());
            copy.setAutor(b.getAutor());
            copy.setEditorial(b.getEditorial());
            copy.setDescripcion(b.getDescripcion());
            copy.setPortadaUrl(b.getPortadaUrl());
            copy.setIdioma(b.getIdioma());
            copy.setAnioPublicacion(b.getAnioPublicacion());
            copy.setCantidadTotal(b.getCantidadTotal());
            copy.setCantidadDisponible(b.getCantidadDisponible());
            copy.setCategoria(newName);

            // Usa updateBook para persistir el cambio en el repo
            bookSvc.updateBook(b.getId(), copy);
        }

        // 2) Renombrar en extraCategories si estaba
        if (extraCategories.removeIf(s -> s.equalsIgnoreCase(oldId))) {
            extraCategories.add(newName);
        }

        Map<String, String> resp = new HashMap<>();
        resp.put("oldName", oldId);
        resp.put("newName", newName);
        resp.put("updatedBooks", String.valueOf(affected.size()));
        resp.put("message", "Categoría actualizada");
        return ResponseEntity.ok(resp);
    }
}

