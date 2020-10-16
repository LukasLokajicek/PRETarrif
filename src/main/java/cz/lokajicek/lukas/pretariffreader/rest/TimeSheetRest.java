package cz.lokajicek.lukas.pretariffreader.rest;

import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import cz.lokajicek.lukas.pretariffreader.service.TariffChecker;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/timesheet")
public class TimeSheetRest {


    private final TariffChecker tariffChecker;

    public TimeSheetRest(TariffChecker tariffChecker) {
        this.tariffChecker = tariffChecker;
    }

    @GetMapping("/{tariff}/today")
    public Collection<StateHour> getStateHours(@PathVariable int tariff) {
        return tariffChecker.getDayTimeSheet(LocalDate.now(), tariff);
    }

    @GetMapping("/{tariff}/{date}")
    public Collection<StateHour> getStateHours(@PathVariable int tariff,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date) {
        return tariffChecker.getDayTimeSheet(date, tariff);
    }
}
