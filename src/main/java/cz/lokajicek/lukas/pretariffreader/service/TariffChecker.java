package cz.lokajicek.lukas.pretariffreader.service;

import cz.lokajicek.lukas.pretariffreader.config.DayType;
import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class TariffChecker {

    private final ExcelLoaderService loaderService;
    private final ExcelReaderService readerService;
    private final PublicHolidayService holidayService;
    private final DayType dayType;
    private Sheet sheet;

    public TariffChecker(ExcelLoaderService loaderService,
                         ExcelReaderService readerService,
                         PublicHolidayService holidayService,
                         DayType dayType) {
        this.loaderService = loaderService;
        this.readerService = readerService;
        this.holidayService = holidayService;
        this.dayType = dayType;
        reloadFirstSheet();
    }

    private void reloadFirstSheet() {
        sheet = loaderService.loadFirstSheet().orElseThrow(() -> new RuntimeException("There is no valid resource."));
    }

    public boolean isSheetValid() {
        return isSheetValid(LocalDate.now());
    }

    public boolean isSheetValid(LocalDate examinedDay) {
        return readerService
                .getValidTo(Optional.of(sheet))
                .map(date -> date.compareTo(examinedDay) > 0)
                .orElse(false);
    }

    public Set<StateHour> getDayTimeSheet(LocalDate localDate, int command) {
        checkSheetValidity(localDate);
        final Map<String, Set<StateHour>> timeSheet = readerService.getTimeSheet(Optional.of(sheet), command);
        if (holidayService.isHoliday(localDate)) return timeSheet.get(dayType.getHoliday());
        return timeSheet.get(dayType.getDayTypeFromDayOfWeek(localDate.getDayOfWeek()));
    }

    private void checkSheetValidity(LocalDate localDate) {
        if (!isSheetValid(localDate)) {
            reloadFirstSheet();
            if (!isSheetValid(localDate)) {
                throw new RuntimeException("Cannot load up-to-date excel sheet from RPE");
            }
        }
    }

}
