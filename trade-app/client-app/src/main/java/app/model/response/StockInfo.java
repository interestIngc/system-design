package app.model.response;

public class StockInfo {
    String company;
    int count;
    int price;

    public StockInfo() {}

    public StockInfo(String company, int count, int price) {
        this.company = company;
        this.count = count;
        this.price = price;
    }

    public String getCompany() {
        return company;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean equals(Object other) {
        if (other instanceof StockInfo) {
            StockInfo otherStockInfo = (StockInfo) other;
            return company.equals(otherStockInfo.getCompany())
                    && count == otherStockInfo.getCount()
                    && price == otherStockInfo.getPrice();
        }
        return false;
    }
}
