package cz.lokajicek.lukas.pretariffreader.service;

import cz.lokajicek.lukas.pretariffreader.config.ExcelLocationConfiguration;
import cz.lokajicek.lukas.pretariffreader.model.State;
import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@Slf4j
public class ExcelReaderService {


    private final ExcelLocationConfiguration locationConfiguration;

    public ExcelReaderService(ExcelLocationConfiguration locationConfiguration) {
        this.locationConfiguration = locationConfiguration;
    }

    public Optional<LocalDate> getValidFrom(Optional<Sheet> sheet) {
        return getLocalDateAtLocation(sheet, locationConfiguration.getValidFrom());
    }


    public Optional<LocalDate> getValidTo(Optional<Sheet> sheet) {
        return getLocalDateAtLocation(sheet, locationConfiguration.getValidTo());
    }

    private Optional<LocalDate> getLocalDateAtLocation(Optional<Sheet> sheet, ExcelLocationConfiguration.CellLocation validFromLocation) {
        final Optional<Row> row = sheet.map(s -> s.getRow(validFromLocation.getRow()));
        final Optional<Cell> cell = row.map(r -> r.getCell(validFromLocation.getColumn()));
        return getLocalDateFromCell(cell);
    }

    private Optional<LocalDate> getLocalDateFromCell(Optional<Cell> cell) {
        return cell
                .map(Cell::getDateCellValue)
                .map(Date::toInstant)
                .map(instant -> instant.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate);
    }

    public Map<Integer, Integer> getAllCommandRowPairs(Optional<Sheet> sheet) {
        return sheet.map(this::extractCommandRowPairs)
                .orElseGet(HashMap::new);
    }

    private Map<Integer, Integer> extractCommandRowPairs(Sheet sheet) {
        AtomicInteger rowCnt = new AtomicInteger(locationConfiguration.getFirstTariffRow());
        final int commandNumberColumn = locationConfiguration.getCommandNumberColumn();
        Map<Integer, Integer> commands = new HashMap<>();
        Optional<Cell> cell;
        do {
            cell = Optional.ofNullable(sheet.getRow(rowCnt.get()))
                    .map(s -> s.getCell(commandNumberColumn, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK));

            cell.ifPresent(
                    c -> commands.put(
                            new Double(c.getNumericCellValue()).intValue(),
                            rowCnt.getAndAdd(locationConfiguration.getRowPeriod())
                    ));
        } while (cell.isPresent());
        return commands;
    }

    public Map<String, Set<StateHour>> getTimeSheet(Optional<Sheet> sheet, int command) {
        Map<String, Set<StateHour>> map = new HashMap<>();
        sheet.ifPresent(s -> {
            final Integer rowNumber = extractCommandRowPairs(s).get(command);
            if (rowNumber != null) {
                for (int cnt = 0; cnt < 8; cnt++) {
                    final Row row = s.getRow(rowNumber + cnt);
                    final Cell dayCell = row.getCell(locationConfiguration.getDayColumn(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    final String dayString = dayCell.getStringCellValue();
                    final Set<StateHour> stateHours = readOnOffSequences(row);
                    map.put(dayString, stateHours);
                }
            }
        });
        return map;
    }

    private Set<StateHour> readOnOffSequences(Row row) {
        Set<StateHour> stateHours = new TreeSet<>();
        final Integer min = Stream.of(locationConfiguration.getOnColumns(), locationConfiguration.getOffColumns())
                .flatMap(Collection::stream)
                .min(Comparators.comparable())
                .orElse(0);
        addStates(row, stateHours, locationConfiguration::getOnColumns, State.ON, min);
        addStates(row, stateHours, locationConfiguration::getOffColumns, State.OFF, min);
        return stateHours;
    }

    private void addStates(Row row, Set<StateHour> stateHours, Supplier<List<Integer>> columnsSupplier, State state, Integer shift) {
        for (Integer onColumn : columnsSupplier.get()) {
            final LocalTime time = readTime(row, onColumn);
            if (time == null) break;
            stateHours.add(new StateHour(state, time, onColumn - shift));
        }
    }


    private LocalTime readTime(Row row, int onColumn) {
        final Cell timeCell = row.getCell(onColumn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (timeCell == null)
            return null;
        Instant instant = Instant.ofEpochMilli(timeCell.getDateCellValue().getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
    }


}
