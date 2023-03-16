package app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("stocks")
public class Stock {
    @Id
    private String company;
    private int price;
    private int count;

    public Stock() {}

    public Stock(String company, int price, int count) {
        this.company = company;
        this.price = price;
        this.count = count;
    }

    public String getCompany() {
        return company;
    }

    public int getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int count) {
        this.count += count;
    }

    public void subtractCount(int count) {
        this.count -= count;
    }
}
