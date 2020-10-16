package cz.lokajicek.lukas.pretariffreader.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Service
public class PublicHolidayService {


    public boolean isHoliday(LocalDate date) {
        if (getStableDays(date.getYear()).contains(date)) {
            return true;
        } else {
            return getDaysInLeapYear(date.getYear()).contains(date);
        }
    }

    private Set<LocalDate> getStableDays(int year) {
        Set<LocalDate> stableDays = new HashSet<>();
        stableDays.add(LocalDate.of(year, Month.JANUARY, 1));
        stableDays.add(LocalDate.of(year, Month.MAY, 1));
        stableDays.add(LocalDate.of(year, Month.MAY, 8));
        stableDays.add(LocalDate.of(year, Month.JULY, 5));
        stableDays.add(LocalDate.of(year, Month.JULY, 6));
        stableDays.add(LocalDate.of(year, Month.SEPTEMBER, 28));
        stableDays.add(LocalDate.of(year, Month.OCTOBER, 28));
        stableDays.add(LocalDate.of(year, Month.NOVEMBER, 17));
        stableDays.add(LocalDate.of(year, Month.DECEMBER, 24));
        stableDays.add(LocalDate.of(year, Month.DECEMBER, 25));
        stableDays.add(LocalDate.of(year, Month.DECEMBER, 26));
        return stableDays;
    }

    private Set<LocalDate> getDaysInLeapYear(int year) {
        Set<LocalDate> leapDays = new HashSet<>();
        //sunday
        final LocalDate easterDate = getEasterDate(year);
        //friday
        leapDays.add(easterDate.minusDays(2));
        //monday
        leapDays.add(easterDate.plusDays(1));

        return leapDays;
    }

    private LocalDate getEasterDate(int year) {
        int month = 3,
                golden = year % 19 + 1,
                century = year / 100 + 1,
                X = (3 * century) / 4 - 12,
                Y = (8 * century + 5) / 25 - 5,
                sunday = (5 * year) / 4 - X - 10,
                epact = (11 * golden + 20 + Y - X) % 30;

        if (epact == 24) {
            epact++;
        }

        if ((epact == 25) && (golden > 11)) {
            epact++;
        }

        int n = 44 - epact;

        if (n < 21) {
            n = n + 30;
        }

        int p = (n + 7) - ((sunday + n) % 7);

        if (p > 31) {
            p -= 31;
            month = 4;
        }

        return LocalDate.of(year, month, p);
    }
}
