package cz.lokajicek.lukas.pretariffreader.controller;

import cz.lokajicek.lukas.pretariffreader.model.StateHour;
import cz.lokajicek.lukas.pretariffreader.service.TariffCheckerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.Collection;

@Controller
public class TableTimeSheetViewController {

    private final TariffCheckerService tariffChecker;

    public TableTimeSheetViewController(TariffCheckerService tariffChecker) {
        this.tariffChecker = tariffChecker;
    }


    @GetMapping("/timesheet/{tariff}/today")
    public String tableView(@PathVariable int tariff, Model model) {
        final LocalDate now = LocalDate.now();
        final Collection<StateHour> timeSheet = tariffChecker.getDayTimeSheet(now, tariff);
        model.addAttribute("timeSheet", timeSheet);
        model.addAttribute("date", now);
        model.addAttribute("tariff", tariff);
        return "tableTimeSheet";
    }
}
