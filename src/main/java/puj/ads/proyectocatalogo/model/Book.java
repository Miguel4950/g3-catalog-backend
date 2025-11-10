package puj.ads.proyectocatalogo.model;

import java.util.Objects;

/**
 * Representa un libro dentro del catálogo. Es una versión simple sin
 * anotaciones de JPA para facilitar su uso en memoria.
 */
public class Book {

    private Integer id_libro;
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private String ano_publicacion; // Se mantiene como cadena para conservar compatibilidad.
    private Integer id_categoria;
    private String descripcion;
    private String portada_url;
    private Integer cantidad_total;
    private Integer cantidad_disponible;

    public Integer getId_libro() {
        return id_libro;
    }

    public void setId_libro(Integer id_libro) {
        this.id_libro = id_libro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getAno_publicacion() {
        return ano_publicacion;
    }

    public void setAno_publicacion(String ano_publicacion) {
        this.ano_publicacion = ano_publicacion;
    }

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPortada_url() {
        return portada_url;
    }

    public void setPortada_url(String portada_url) {
        this.portada_url = portada_url;
    }

    public Integer getCantidad_total() {
        return cantidad_total;
    }

    public void setCantidad_total(Integer cantidad_total) {
        this.cantidad_total = cantidad_total;
    }

    public Integer getCantidad_disponible() {
        return cantidad_disponible;
    }

    public void setCantidad_disponible(Integer cantidad_disponible) {
        this.cantidad_disponible = cantidad_disponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(id_libro, book.id_libro) &&
                Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_libro, isbn);
    }
}
