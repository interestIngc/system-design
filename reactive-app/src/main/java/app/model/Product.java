package app.model;

import org.bson.Document;

public class Product {
    private String name;
    private double price;
    private Currency currency;

    public Product(Document document) {
        this(
                document.getString("name"),
                document.getDouble("price"),
                Currency.valueOf(document.getString("currency"))
        );
    }

    public Product(String name, double price, Currency currency) {
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public double getPriceIn(Currency currency) {
        return price * this.currency.in(currency);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{\n" +
                String.format("\tname: %s;\n", name) +
                String.format("\tprice: %f %s;\n", price, currency.toString()) +
                "}\n";
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("price", price)
                .append("currency", currency.name());
    }
}
