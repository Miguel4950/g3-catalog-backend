package puj.ads.proyectocatalogo.model;

import java.util.List;

public class PagedResult<T> {
    public List<T> items;
    public int page;
    public int limit;
    public long total;
    public int totalPages;
}
