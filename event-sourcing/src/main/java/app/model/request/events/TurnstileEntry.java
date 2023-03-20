package app.model.request.events;

import java.time.LocalDateTime;

public class TurnstileEntry extends Event {
    public TurnstileEntry() {}

    public TurnstileEntry(int subscriptionId, LocalDateTime timestamp) {
        super(subscriptionId, timestamp);
    }
}
