package app.model.request.commands;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "commands")
public class Command {
    private int subscriptionId;

    public Command() {}

    public Command(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int subscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
