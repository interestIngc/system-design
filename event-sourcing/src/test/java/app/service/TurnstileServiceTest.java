package app.service;

import app.model.request.events.TurnstileEntry;
import app.model.request.events.TurnstileExit;
import app.model.response.admin.SubscriptionInfo;
import app.repository.EventRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static app.service.AdminService.AdminServiceException;
import static app.service.TurnstileService.TurnstileServiceException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TurnstileServiceTest {
    private static final int SUBSCRIPTION_ID = 0;
    private static final LocalDateTime EXPIRY_TIME =
            LocalDateTime.of(2023, 3, 20, 12, 0);
    private static final LocalDateTime TIMESTAMP_1 =
            LocalDateTime.of(2023, 3, 15, 12, 0);
    private static final LocalDateTime TIMESTAMP_2 =
            LocalDateTime.of(2023, 3, 21, 12, 0);
    @Mock
    private AdminService adminService;
    @Mock
    private EventRepository eventRepository;

    private TurnstileService turnstileService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        turnstileService = new TurnstileService(adminService, eventRepository);
    }

    @Test
    public void letIn_entersForTheFirstTimeAndSubscriptionIsNotTimedOut_letsInTheClient()
            throws AdminServiceException, TurnstileServiceException {
        when(eventRepository.findAll(any(Example.class), any(Sort.class)))
                .thenReturn(List.of());
        when(adminService.getSubscriptionInfo(SUBSCRIPTION_ID))
                .thenReturn(new SubscriptionInfo(SUBSCRIPTION_ID, EXPIRY_TIME));

        turnstileService.letIn(SUBSCRIPTION_ID, TIMESTAMP_1);

        verify(eventRepository).insert(any(TurnstileEntry.class));
    }

    @Test(expected = TurnstileServiceException.class)
    public void letIn_alreadyEntered_doesNotLetInTheClient() throws TurnstileServiceException {
        when(eventRepository.findAll(any(Example.class), any(Sort.class)))
                .thenReturn(List.of(new TurnstileEntry(SUBSCRIPTION_ID, TIMESTAMP_1)));

        turnstileService.letIn(SUBSCRIPTION_ID, TIMESTAMP_1);
    }

    @Test(expected = TurnstileServiceException.class)
    public void letIn_subscriptionIsTimedOut_doesNotLetInTheClient()
            throws TurnstileServiceException, AdminServiceException {
        when(eventRepository.findAll(any(Example.class), any(Sort.class)))
                .thenReturn(List.of());
        when(adminService.getSubscriptionInfo(SUBSCRIPTION_ID))
                .thenReturn(new SubscriptionInfo(SUBSCRIPTION_ID, EXPIRY_TIME));

        turnstileService.letIn(SUBSCRIPTION_ID, TIMESTAMP_2);
    }

    @Test
    public void letOut_clientEntered_letsOutTheClient() throws TurnstileServiceException {
        when(adminService.exists(SUBSCRIPTION_ID)).thenReturn(true);
        when(eventRepository.findAll(any(Example.class), any(Sort.class)))
                .thenReturn(List.of(new TurnstileEntry(SUBSCRIPTION_ID, TIMESTAMP_1)));

        turnstileService.letOut(SUBSCRIPTION_ID, TIMESTAMP_2);

        verify(eventRepository).insert(any(TurnstileExit.class));
    }

    @Test(expected = TurnstileServiceException.class)
    public void letOut_clientDoesNotExistInAdminSystem_doesNotLetOutTheClient()
            throws TurnstileServiceException {
        when(adminService.exists(SUBSCRIPTION_ID)).thenReturn(false);

        turnstileService.letOut(SUBSCRIPTION_ID, TIMESTAMP_1);
    }

    @Test(expected = TurnstileServiceException.class)
    public void letOut_clientDidNotEnter_doesNotLetOutTheClient()
            throws TurnstileServiceException {
        when(adminService.exists(SUBSCRIPTION_ID)).thenReturn(true);
        when(eventRepository.findAll(any(Example.class), any(Sort.class)))
                .thenReturn(List.of());

        turnstileService.letOut(SUBSCRIPTION_ID, TIMESTAMP_1);
    }
}