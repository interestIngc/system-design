package app.model.response.admin;

import java.time.LocalDateTime;

public class SubscriptionInfo {
    private final int subscriptionId;
    private final LocalDateTime expiryTime;

    public SubscriptionInfo(int subscriptionId, LocalDateTime expiryTime) {
        this.subscriptionId = subscriptionId;
        this.expiryTime = expiryTime;
    }

    public int subscriptionId() {
        return subscriptionId;
    }

    public LocalDateTime expiryTime() {
        return expiryTime;
    }
}
