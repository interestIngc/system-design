package app.service;

import app.model.request.events.TurnstileEntry;
import app.model.request.events.TurnstileExit;
import app.model.response.report.DayReport;
import app.model.response.report.StatisticsReport;
import app.repository.EventRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ReportServiceTest {
    private static final int SUBSCRIPTION_ID_1 = 0;
    private static final int SUBSCRIPTION_ID_2 = 1;
    private static final LocalDate DATE_1 = LocalDate.of(2023, 3, 15);
    private static final LocalDate DATE_2 = LocalDate.of(2023, 3, 16);
    private static final LocalDate DATE_3 = LocalDate.of(2023, 3, 17);
    private static final LocalTime TIME_1 = LocalTime.of(12, 0);
    private static final LocalTime TIME_2 = LocalTime.of(13, 0);
    private static final LocalTime TIME_3 = LocalTime.of(14, 0);

    @Mock private EventRepository eventRepository;
    private ReportService reportService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reportService = new ReportService(eventRepository);
    }

    @Test
    public void getStatisticsReport_entriesInDifferentDays_returnsStatisticsForAllDays() {
        when(eventRepository.findAll(any(Sort.class)))
                .thenReturn(
                        List.of(
                                new TurnstileEntry(SUBSCRIPTION_ID_1, LocalDateTime.of(DATE_1, TIME_1)),
                                new TurnstileExit(SUBSCRIPTION_ID_1, LocalDateTime.of(DATE_1, TIME_3)),
                                new TurnstileEntry(SUBSCRIPTION_ID_2, LocalDateTime.of(DATE_3, TIME_1)),
                                new TurnstileExit(SUBSCRIPTION_ID_2, LocalDateTime.of(DATE_3, TIME_2))
                        )
                );

        StatisticsReport report = reportService.getStatisticsReport();

        DayReport dayReport1 = new DayReport(DATE_1, 1, 7200000);
        DayReport dayReport2 = new DayReport(DATE_2);
        DayReport dayReport3 = new DayReport(DATE_3, 1, 3600000);
        assertThat(report.getDayReports())
                .containsExactly(dayReport1, dayReport2, dayReport3);
    }

    @Test
    public void getStatisticsReport_twoVisitsInOneDay_returnsCorrectStatisticsForOneDay() {
        when(eventRepository.findAll(any(Sort.class)))
                .thenReturn(
                        List.of(
                                new TurnstileEntry(SUBSCRIPTION_ID_1, LocalDateTime.of(DATE_1, TIME_1)),
                                new TurnstileEntry(SUBSCRIPTION_ID_2, LocalDateTime.of(DATE_1, TIME_1)),
                                new TurnstileExit(SUBSCRIPTION_ID_2, LocalDateTime.of(DATE_1, TIME_2)),
                                new TurnstileExit(SUBSCRIPTION_ID_1, LocalDateTime.of(DATE_1, TIME_3))
                        )
                );

        StatisticsReport report = reportService.getStatisticsReport();

        DayReport dayReport1 = new DayReport(DATE_1, 2, 5400000);
        assertThat(report.getDayReports()).containsExactly(dayReport1);
    }
}
