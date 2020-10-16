package cz.lokajicek.lukas.pretariffreader.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExcelLoaderService {
    private final ExcelFetcherService excelReader;

    public ExcelLoaderService(ExcelFetcherService excelReader) {
        this.excelReader = excelReader;
    }

    public Optional<Sheet> loadFirstSheet() {
        Workbook workbook = excelReader.readFromServer();

        return Optional.ofNullable(workbook)
                .map(w -> w.getSheetAt(0));
    }
}
