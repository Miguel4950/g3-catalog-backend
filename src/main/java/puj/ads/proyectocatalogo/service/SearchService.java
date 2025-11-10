package puj.ads.proyectocatalogo.service;

import org.springframework.stereotype.Component;
import puj.ads.proyectocatalogo.model.Book;

import java.util.List;

@Component
public class SearchService {

    public List<Book> search(List<Book> books, String query, String category, Boolean available) {
        return books.stream()
                .filter(b -> query == null || query.isBlank()
                        || (b.getTitulo()!=null && b.getTitulo().toLowerCase().contains(query.toLowerCase()))
                        || (b.getAutor()!=null && b.getAutor().toLowerCase().contains(query.toLowerCase())))
                .filter(b -> category == null || category.isBlank() || (b.getCategoria()!=null && b.getCategoria().equalsIgnoreCase(category)))
                .filter(b -> available == null || (available && b.getCantidadDisponible() > 0) || (!available && b.getCantidadDisponible()==0))
                .toList();
    }
}
