package cz.lokajicek.lukas.pretariffreader.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ExcelFetcherService {

    @Value("${app.excel.resource}")
    Resource resource;

    public Workbook readFromServer() {
        return Optional.ofNullable(resource)
                .map(this::getWorkBook)
                .orElse(null);
    }

    private Workbook getWorkBook(@NonNull Resource resource) {
        try (final Workbook workbook = WorkbookFactory.create(resource.getInputStream())) {
            return workbook;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
