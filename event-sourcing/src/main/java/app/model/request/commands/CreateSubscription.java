package app.model.request.commands;

import java.time.LocalDateTime;

public class CreateSubscription extends Command {
    private LocalDateTime expiryTime;

    public CreateSubscription() {}

    public CreateSubscription(int subscriptionId) {
        super(subscriptionId);
    }

    public CreateSubscription(int subscriptionId, LocalDateTime expiryTime) {
        super(subscriptionId);
        this.expiryTime = expiryTime;
    }

    public LocalDateTime expiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
