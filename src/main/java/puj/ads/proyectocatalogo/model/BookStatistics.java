package puj.ads.proyectocatalogo.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookStatistics {
    Long bookId;
    Long prestamosActivos;
    Long prestamosVencidos;
    Integer cantidadTotal;
    Integer cantidadDisponible;
}
