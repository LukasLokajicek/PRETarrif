package cz.lokajicek.lukas.pretariffreader.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PublicHolidayServiceTest {

    @Autowired
    private PublicHolidayService holidayService;

    @Test
    void isHoliday() {
        Assertions.assertTrue(holidayService.isHoliday(LocalDate.of(2021, 4, 2)), "Easter holiday was not found for 2021 year");
        Assertions.assertTrue(holidayService.isHoliday(LocalDate.of(2021, 4, 5)), "Easter holiday was not found for 2021 year");
        Assertions.assertTrue(holidayService.isHoliday(LocalDate.of(2025, 1, 1)), "1.1. is public holiday!");
        Assertions.assertTrue(holidayService.isHoliday(LocalDate.of(2020, 9, 28)), "28.9. is public holiday!");
        Assertions.assertFalse(holidayService.isHoliday(LocalDate.of(2026, 9, 27)), "27.9. is not public holiday!");
    }
}