package app.model.request.events;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value = "events")
public class Event {
    private int subscriptionId;
    private LocalDateTime timestamp;

    public Event() {}

    public Event(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Event(int subscriptionId, LocalDateTime timestamp) {
        this.subscriptionId = subscriptionId;
        this.timestamp = timestamp;
    }

    public int subscriptionId() {
        return subscriptionId;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
