package cz.lokajicek.lukas.pretariffreader.service;

import cz.lokajicek.lukas.pretariffreader.model.State;
import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@SpringBootTest
class TariffCheckerServiceTest {

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private TariffCheckerService tariffChecker;

    @Test
    void isSheetValid() {
        Assertions.assertFalse(tariffChecker.isSheetValid(LocalDate.of(2020, 10, 23)));
        Assertions.assertTrue(tariffChecker.isSheetValid(LocalDate.of(2020, 10, 22)));
        Assertions.assertTrue(tariffChecker.isSheetValid(LocalDate.of(2020, 10, 21)));
    }

    @Test
    void getDayTimeSheet() {
        final Set<StateHour> dayTimeSheet = tariffChecker.getDayTimeSheet(LocalDate.of(2020, 10, 15), 573);
        logger.info("Timesheet for 15. 10. 2020 is: {}.", dayTimeSheet);
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(0, 0), 0)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(1, 0), 1)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(1, 40), 2)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(6, 40), 3)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(7, 20), 4)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(11, 0), 5)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(11, 40), 6)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(14, 0), 7)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(14, 40), 8)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(17, 40), 9)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(18, 20), 10)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(21, 20), 11)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.OFF, LocalTime.of(22, 0), 12)));
        Assertions.assertTrue(dayTimeSheet.contains(new StateHour(State.ON, LocalTime.of(0, 0), 13)));
    }
}