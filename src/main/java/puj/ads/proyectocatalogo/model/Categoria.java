package puj.ads.proyectocatalogo.model;

import java.util.Objects;

/**
 * Entidad simple para representar categorías sin depender de JPA.
 */
public class Categoria {
    private Integer id_categoria;
    private String nombre;
    private String descripcion;

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id_categoria, categoria.id_categoria) &&
                Objects.equals(nombre, categoria.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_categoria, nombre);
    }
}
