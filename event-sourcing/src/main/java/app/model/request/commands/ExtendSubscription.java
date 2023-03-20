package app.model.request.commands;

import java.time.Duration;

public class ExtendSubscription extends Command {
    private Duration duration;

    public ExtendSubscription() {}

    public ExtendSubscription(int subscriptionId) {
        super(subscriptionId);
    }

    public ExtendSubscription(int subscriptionId, Duration duration) {
        super(subscriptionId);
        this.duration = duration;
    }

    public Duration duration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
