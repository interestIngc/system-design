package app.model;

import org.bson.Document;

public class User {
    private int id;
    private Currency currency;

    public User(Document document) {
        this(
                document.getInteger("id"),
                Currency.valueOf(document.getString("currency"))
        );
    }

    public User(int id, Currency currency) {
        this.id = id;
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{\n" +
                String.format("\tid: %d;\n", id) +
                String.format("\tcurrency: %s;\n", currency.toString()) +
                "}\n";

    }

    public Document toDocument() {
        return new Document("id", id).append("currency", currency.name());
    }
}
