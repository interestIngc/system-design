package app.model.response;

import java.util.ArrayList;
import java.util.List;

public class AccountInfo {
    private int id;
    private int amount;
    private List<StockInfo> stockInfos;

    public AccountInfo(int id) {
        this.id = id;
        this.stockInfos = new ArrayList<>();
    }

    public AccountInfo(int id, int amount, List<StockInfo> stockInfos) {
        this.id = id;
        this.amount = amount;
        this.stockInfos = stockInfos;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public List<StockInfo> getStockInfos() {
        return stockInfos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStockInfos(List<StockInfo> stockInfos) {
        this.stockInfos = stockInfos;
    }

    public void addStockInfo(StockInfo stockInfo) {
        stockInfos.add(stockInfo);
    }

    public boolean equals(Object other) {
        if (other instanceof AccountInfo) {
            AccountInfo accountInfo = (AccountInfo) other;
            return id == accountInfo.getId()
                    && amount == accountInfo.getAmount()
                    && stockInfos.equals(accountInfo.getStockInfos());
        }
        return false;
    }
}
