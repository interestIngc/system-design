package app.model.response.report;

import java.util.ArrayList;
import java.util.List;

public class StatisticsReport {
    private final List<DayReport> dayReports;

    public StatisticsReport() {
        dayReports = new ArrayList<>();
    }

    public StatisticsReport(List<DayReport> dayReports) {
        this.dayReports = dayReports;
    }

    public void addDayReport(DayReport dayReport) {
        dayReports.add(dayReport);
    }

    public List<DayReport> getDayReports() {
        return dayReports;
    }
}
