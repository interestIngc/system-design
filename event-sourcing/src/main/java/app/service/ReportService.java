package app.service;

import app.model.request.events.Event;
import app.model.request.events.TurnstileEntry;
import app.model.request.events.TurnstileExit;
import app.model.response.report.DayReport;
import app.model.response.report.StatisticsReport;
import app.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportService {
    private final EventRepository eventRepository;
    private final Map<LocalDate, DayReport> dayReports = new TreeMap<>();
    private final Map<Integer, LocalDateTime> entryTimestamps = new HashMap<>();
    private LocalDateTime lastProcessedTimestamp = null;

    @Autowired
    public ReportService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        updateStatistics();
    }

    public StatisticsReport getStatisticsReport() {
        updateStatistics();
        return new StatisticsReport(List.copyOf(dayReports.values()));
    }

    private void updateStatistics() {
        List<Event> events;
        if (lastProcessedTimestamp == null) {
            events = eventRepository.findAll(Sort.by(Sort.Direction.ASC, "timestamp"));
        } else {
            events = eventRepository.findByTimestampGreaterThanOrderByTimestamp(lastProcessedTimestamp);
        }

        for (Event event : events) {
            int subscriptionId = event.subscriptionId();
            LocalDateTime timestamp = event.timestamp();
            LocalDate eventDate = timestamp.toLocalDate();
            if (!dayReports.containsKey(eventDate)) {
                if (lastProcessedTimestamp != null) {
                    for (LocalDate currDate = lastProcessedTimestamp.toLocalDate().plusDays(1);
                         currDate.isBefore(eventDate);
                         currDate = currDate.plusDays(1)) {
                        dayReports.put(currDate, new DayReport(currDate));
                    }
                }
                dayReports.put(eventDate, new DayReport(eventDate));
            }
            DayReport dayReport = dayReports.get(eventDate);

            if (event instanceof TurnstileEntry) {
                entryTimestamps.put(subscriptionId, timestamp);
            } else if (event instanceof TurnstileExit) {
                Duration duration = Duration.between(entryTimestamps.get(subscriptionId), timestamp);
                dayReport.onExit(duration);
                entryTimestamps.remove(subscriptionId);
            }

            lastProcessedTimestamp = timestamp;
        }
    }
}
