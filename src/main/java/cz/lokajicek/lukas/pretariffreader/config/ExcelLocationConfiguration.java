package cz.lokajicek.lukas.pretariffreader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "app.excel.location")
@Component
@Data
public class ExcelLocationConfiguration {

    private CellLocation validFrom;
    private CellLocation validTo;
    private Integer commandNumberColumn;
    private Integer dayColumn;
    private Integer firstTariffRow = 0;
    private Integer rowPeriod;
    private List<Integer> onColumns;
    private List<Integer> offColumns;


    @Data
    public static class CellLocation {

        private Integer column;
        private Integer row;

    }
}
