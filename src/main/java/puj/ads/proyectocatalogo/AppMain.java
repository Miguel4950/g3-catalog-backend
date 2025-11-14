package puj.ads.proyectocatalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppMain {
    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
        System.out.println("Catálogo iniciado en http://localhost:8082");
    }
}
