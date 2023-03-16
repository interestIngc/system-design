package app.model.account;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document("accounts")
public class Account {
    @Id
    private int id;
    private int amount;
    private Map<String, Integer> companyStocks;

    public Account() {
        companyStocks = new HashMap<>();
    }

    public Account(int id) {
        this.id = id;
        this.companyStocks = new HashMap<>();
    }

    public Account(int id, int amount) {
        this(id, amount, new HashMap<>());
    }

    public Account(int id, int amount, Map<String, Integer> companyStocks) {
        this.id = id;
        this.amount = amount;
        this.companyStocks = companyStocks;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public Map<String, Integer> getCompanyStocks() {
        return companyStocks;
    }

    public int getStocks(String company) {
        return companyStocks.getOrDefault(company, 0);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void subtractAmount(int amount) {
        this.amount -= amount;
    }

    public void setCompanyStocks(Map<String, Integer> companyStocks) {
        this.companyStocks = companyStocks;
    }

    public void addStocks(String company, int count) {
        companyStocks.compute(
                company,
                (k, currCount) -> currCount == null ? count : currCount + count
        );
    }

    public boolean equals(Object other) {
        if (other instanceof Account) {
            Account otherAccount = (Account) other;
            return id == otherAccount.getId()
                    && amount == otherAccount.getAmount()
                    && companyStocks.equals(otherAccount.getCompanyStocks());
        }
        return false;
    }
}
