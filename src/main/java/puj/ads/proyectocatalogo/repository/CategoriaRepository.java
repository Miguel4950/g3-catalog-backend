package puj.ads.proyectocatalogo.repository;

import puj.ads.proyectocatalogo.model.Categoria;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria para las categorías.
 */
public class CategoriaRepository {

    private final Map<Integer, Categoria> storage = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    public synchronized Categoria save(Categoria categoria) {
        if (categoria.getId_categoria() == null) {
            categoria.setId_categoria(sequence.getAndIncrement());
        }
        Categoria copy = copyOf(categoria);
        storage.put(copy.getId_categoria(), copy);
        return copyOf(copy);
    }

    public synchronized Optional<Categoria> findById(int id) {
        return Optional.ofNullable(storage.get(id)).map(CategoriaRepository::copyOf);
    }

    public synchronized Optional<Categoria> findByNombreIgnoreCase(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        String normalized = nombre.toLowerCase(Locale.ROOT);
        return storage.values().stream()
                .filter(cat -> cat.getNombre() != null && cat.getNombre().toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .map(CategoriaRepository::copyOf);
    }

    public synchronized List<Categoria> findAll() {
        return storage.values().stream()
                .map(CategoriaRepository::copyOf)
                .collect(Collectors.toList());
    }

    public synchronized void deleteById(int id) {
        storage.remove(id);
    }

    private static Categoria copyOf(Categoria categoria) {
        Categoria copy = new Categoria();
        copy.setId_categoria(categoria.getId_categoria());
        copy.setNombre(categoria.getNombre());
        copy.setDescripcion(categoria.getDescripcion());
        return copy;
    }
}
