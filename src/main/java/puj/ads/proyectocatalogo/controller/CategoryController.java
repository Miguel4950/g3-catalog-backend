package puj.ads.proyectocatalogo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import puj.ads.proyectocatalogo.model.Category;
import puj.ads.proyectocatalogo.service.CategoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> list() {
        return categoryService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category create(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Category rename(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoNombre = body.get("name");
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        return categoryService.rename(id, nuevoNombre);
    }
}
