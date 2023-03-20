package app.model.request.events;

import java.time.LocalDateTime;

public class TurnstileExit extends Event {
    public TurnstileExit() {}

    public TurnstileExit(int subscriptionId, LocalDateTime timestamp) {
        super(subscriptionId, timestamp);
    }
}
