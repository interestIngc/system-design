package app.controller;

import app.model.response.report.StatisticsReport;
import app.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("report")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/stat", produces = "application/json")
    public StatisticsReport getStat() {
        return reportService.getStatisticsReport();
    }
}
