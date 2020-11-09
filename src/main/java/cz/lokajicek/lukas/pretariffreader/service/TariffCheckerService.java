package cz.lokajicek.lukas.pretariffreader.service;

import cz.lokajicek.lukas.pretariffreader.config.DayType;
import cz.lokajicek.lukas.pretariffreader.model.State;
import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
public class TariffCheckerService {

    private final ExcelLoaderService loaderService;
    private final ExcelReaderService readerService;
    private final PublicHolidayService holidayService;
    private final DayType dayType;
    private Sheet sheet;

    public TariffCheckerService(ExcelLoaderService loaderService,
                                ExcelReaderService readerService,
                                PublicHolidayService holidayService,
                                DayType dayType) {
        this.loaderService = loaderService;
        this.readerService = readerService;
        this.holidayService = holidayService;
        this.dayType = dayType;
    }

    private void reloadFirstSheet() {
        sheet = loaderService.loadFirstSheet().orElseThrow(() -> new RuntimeException("There is no valid resource."));
    }

    public boolean isSheetValid(LocalDate examinedDay) {
        return readerService
                .getValidTo(Optional.of(getSheet()))
                .map(date -> date.compareTo(examinedDay) >= 0)
                .orElse(false);
    }

    public SortedSet<StateHour> getDayTimeSheet(LocalDate localDate, int command) {
        checkSheetValidity(localDate);
        final Map<String, SortedSet<StateHour>> timeSheet = readerService.getTimeSheet(Optional.of(getSheet()), command);
        return getStateHours(localDate, timeSheet);
    }

    private SortedSet<StateHour> getStateHours(LocalDate localDate, Map<String, SortedSet<StateHour>> timeSheet) {
        final SortedSet<StateHour> stateHours;
        if (holidayService.isHoliday(localDate))
            stateHours = timeSheet.get(dayType.getHoliday());
        else
            stateHours = timeSheet.get(dayType.getDayTypeFromDayOfWeek(localDate.getDayOfWeek()));
        return stateHours;
    }

    private void checkSheetValidity(LocalDate localDate) {
        if (!isSheetValid(localDate)) {
            reloadFirstSheet();
            if (!isSheetValid(localDate)) {
                throw new RuntimeException("Cannot load up-to-date excel sheet from RPE");
            }
        }
    }

    private Sheet getSheet() {
        if (sheet == null) {
            reloadFirstSheet();
        }
        return sheet;
    }


    public Long getNextOffTimeInSec(int command, LocalDateTime toDateTime) {
        final LocalDate toLocalDate = toDateTime.toLocalDate();
        //It is sufficient to check only that day
        checkSheetValidity(toLocalDate);
        final Map<String, SortedSet<StateHour>> timeSheet = readerService.getTimeSheet(Optional.of(getSheet()), command);
        final SortedSet<StateHour> thatDay = getStateHours(toLocalDate, timeSheet);
        final SortedSet<StateHour> nextDay = getStateHours(toLocalDate.plusDays(1), timeSheet);
        removeLastMidnight(thatDay);


        return thatDay
                .stream()
                .filter(stateHour -> stateHour.getState().equals(State.OFF) && stateHour.getHour().isAfter(toDateTime.toLocalTime()))
                .min(StateHour::compareTo)
                .map(stateHour -> toDateTime.toLocalTime().until(stateHour.getHour(), ChronoUnit.SECONDS))
                .orElse(
                        nextDay
                                .stream()
                                .filter(stateHour -> stateHour.getState().equals(State.OFF))
                                .min(StateHour::compareTo)
                                .map(stateHour -> toDateTime.until(LocalDateTime.of(toLocalDate.plusDays(1), stateHour.getHour()), ChronoUnit.SECONDS))
                                .orElse(0L)
                );
    }

    private void removeLastMidnight(SortedSet<StateHour> stateHours) {
        final StateHour lastStateHour = stateHours.last();
        if (lastStateHour.getHour().equals(LocalTime.MIDNIGHT)) {
            stateHours.remove(lastStateHour);
        }
    }
}
