package app.service;

import app.model.request.commands.CreateSubscription;
import app.model.request.commands.ExtendSubscription;
import app.model.response.admin.SubscriptionInfo;
import app.repository.CommandRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static app.service.AdminService.AdminServiceException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdminServiceTest {
    private static final int SUBSCRIPTION_ID = 0;
    private static final LocalDateTime EXPIRY_TIME =
            LocalDateTime.of(2023, 3, 20, 12, 0);
    private static final Duration EXTEND_DURATION = Duration.ofDays(10);
    @Mock private CommandRepository commandRepository;
    private AdminService adminService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adminService = new AdminService(commandRepository);
    }

    @Test
    public void test_getSubscriptionInfo() throws AdminServiceException {
        when(commandRepository.exists(any())).thenReturn(true);
        when(commandRepository.findAll(any(Example.class)))
                .thenReturn(
                        List.of(
                                new CreateSubscription(SUBSCRIPTION_ID, EXPIRY_TIME),
                                new ExtendSubscription(SUBSCRIPTION_ID, EXTEND_DURATION)
                        )
                );

        SubscriptionInfo subscriptionInfo =
                adminService.getSubscriptionInfo(SUBSCRIPTION_ID);

        LocalDateTime expectedExpiryTime =
                LocalDateTime.of(2023, 3, 30, 12, 0);
        assertThat(subscriptionInfo.expiryTime()).isEqualTo(expectedExpiryTime);
    }

    @Test
    public void test_createSubscription() throws AdminServiceException {
        when(commandRepository.exists(any())).thenReturn(false);

        adminService.createSubscription(SUBSCRIPTION_ID, EXPIRY_TIME);

        verify(commandRepository).insert(any(CreateSubscription.class));
    }

    @Test
    public void test_extendSubscription() throws AdminServiceException {
        when(commandRepository.exists(any())).thenReturn(true);

        adminService.extendSubscription(SUBSCRIPTION_ID, EXTEND_DURATION);

        verify(commandRepository).insert(any(ExtendSubscription.class));
    }
}
