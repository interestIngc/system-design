package app.service;

import app.model.request.events.Event;
import app.model.request.events.TurnstileEntry;
import app.model.request.events.TurnstileExit;
import app.model.response.admin.SubscriptionInfo;
import app.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnstileService {
    private final AdminService adminService;
    private final EventRepository eventRepository;

    @Autowired
    public TurnstileService(AdminService adminService, EventRepository eventRepository) {
        this.adminService = adminService;
        this.eventRepository = eventRepository;
    }

    public void letIn(int subscriptionId, LocalDateTime timestamp) throws TurnstileServiceException {
        if (entered(subscriptionId)) {
            throw new TurnstileServiceException(
                    String.format(
                            "Cannot let in the client, someone already entered with subscription %d",
                            subscriptionId
                    )
            );
        }

        try {
            SubscriptionInfo subscriptionInfo = adminService.getSubscriptionInfo(subscriptionId);
            if (!timestamp.isBefore(subscriptionInfo.expiryTime())) {
                throw new TurnstileServiceException(
                        String.format(
                                "Cannot let in the client: subscription %d expired",
                                subscriptionId
                        )
                );
            }
            eventRepository.insert(new TurnstileEntry(subscriptionId, timestamp));
        } catch (AdminService.AdminServiceException e) {
            throw new TurnstileServiceException(e.getMessage());
        }
    }

    public void letOut(int subscriptionId, LocalDateTime timestamp) throws TurnstileServiceException {
        if (!adminService.exists(subscriptionId)) {
            throw new TurnstileServiceException(
                    String.format("subscription with id %d does not exist", subscriptionId));
        }
        if (!entered(subscriptionId)) {
            throw new TurnstileServiceException(
                    String.format(
                            "Client with subscription %d exits, but did not enter",
                            subscriptionId
                    )
            );
        }
        eventRepository.insert(new TurnstileExit(subscriptionId, timestamp));
    }

    private boolean entered(int subscriptionId) {
        List<Event> events = eventRepository.findAll(
                Example.of(new Event(subscriptionId)),
                Sort.by(Sort.Direction.ASC, "timestamp"));
        return !events.isEmpty() && (events.get(events.size() - 1) instanceof TurnstileEntry);
    }

    public static class TurnstileServiceException extends Exception {
        public TurnstileServiceException(String message) {
            super(message);
        }
    }
}
