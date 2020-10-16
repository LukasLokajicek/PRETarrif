package cz.lokajicek.lukas.pretariffreader.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExcelReaderServiceTest {

    @Autowired
    private ExcelLoaderService loader;

    @Autowired
    private ExcelReaderService reader;

    private Optional<Sheet> optionalSheet;

    @PostConstruct
    public void prepareSheet() {
        optionalSheet = loader.loadFirstSheet();
    }

    @Test
    void getValidFrom() {
        Assertions.assertEquals(LocalDate.of(2020, 9, 4), reader.getValidFrom(optionalSheet).orElse(null));
    }

    @Test
    void getValidTo() {
        Assertions.assertEquals(LocalDate.of(2020, 10, 22), reader.getValidTo(optionalSheet).orElse(null));
    }

    @Test
    void getAllCommandRowPairs() {
        final Map<Integer, Integer> allCommandRowPairs = reader.getAllCommandRowPairs(optionalSheet);
        Assertions.assertEquals(115, allCommandRowPairs.keySet().size());
        Assertions.assertTrue(allCommandRowPairs.containsKey(573));
        Assertions.assertEquals(531, allCommandRowPairs.get(573));
    }

    @Test
    void getTimeSheet() {
    }
}