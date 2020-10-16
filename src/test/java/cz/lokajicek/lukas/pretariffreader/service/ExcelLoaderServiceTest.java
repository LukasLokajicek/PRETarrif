package cz.lokajicek.lukas.pretariffreader.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class ExcelLoaderServiceTest {

    @Autowired
    private ExcelLoaderService loader;

    @Test
    void loadFirstSheet() {
        final Optional<Sheet> sheet = loader.loadFirstSheet();
        Assertions.assertTrue(sheet.isPresent(), "Error loading pre.xls. Check whether application.yml is correctly set up.");
    }
}