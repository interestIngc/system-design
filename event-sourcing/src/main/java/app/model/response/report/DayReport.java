package app.model.response.report;

import java.time.Duration;
import java.time.LocalDate;

public class DayReport {
    private final LocalDate date;
    private long frequency;
    private double averageDuration;

    public DayReport(LocalDate date) {
        this.date = date;
        this.frequency = 0;
        this.averageDuration = 0;
    }

    public DayReport(LocalDate date, long frequency, double averageDuration) {
        this.date = date;
        this.frequency = frequency;
        this.averageDuration = averageDuration;
    }

    public void onExit(Duration duration) {
        frequency++;
        averageDuration = (averageDuration * (frequency - 1) + duration.toMillis()) / frequency;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DayReport) {
            DayReport otherReport = (DayReport) other;
            return otherReport.date.equals(date)
                    && otherReport.frequency == frequency
                    && otherReport.averageDuration == averageDuration;
        }
        return false;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getFrequency() {
        return frequency;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }
}
