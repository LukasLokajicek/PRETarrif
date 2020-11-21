package cz.lokajicek.lukas.pretariffreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PreTariffReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreTariffReaderApplication.class, args);

    }

}
