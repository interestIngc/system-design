package app.service;

import app.model.Stock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ExchangeService {
    private final MongoTemplate mongoTemplate;

    public ExchangeService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void createCompany(String companyName, int price, int count) {
        mongoTemplate.save(new Stock(companyName, price, count));
    }

    public void addStocks(String companyName, int count) throws StockServiceException {
        Stock stock = findCompanyStock(companyName);
        stock.addCount(count);
        mongoTemplate.save(stock);
    }

    public int getStockPrice(String companyName) throws StockServiceException {
        Stock stock = findCompanyStock(companyName);
        return stock.getPrice();
    }

    public int getStockCount(String companyName) throws StockServiceException {
        Stock stock = findCompanyStock(companyName);
        return stock.getCount();
    }

    public void buyStocks(String companyName, int count)
            throws StockServiceException {
        Stock stock = findCompanyStock(companyName);
        if (stock.getCount() < count) {
            throw new StockServiceException(
                    String.format("Cannot buy %d stocks: only %d available", count, stock.getCount())
            );
        }

        stock.subtractCount(count);
        mongoTemplate.save(stock);
    }

    public void changeStockPrice(String companyName, int newPrice)
            throws StockServiceException {
        Stock stock = findCompanyStock(companyName);
        stock.setPrice(newPrice);
        mongoTemplate.save(stock);
    }

    private Stock findCompanyStock(String companyName) throws StockServiceException {
        Query query = new Query().addCriteria(Criteria.where("company").is(companyName));
        Stock stock = mongoTemplate.findOne(query, Stock.class);

        if (stock == null) {
            throw new StockServiceException("Company does not exist in the system");
        }

        return stock;
    }

    public static class StockServiceException extends Exception {
        public StockServiceException(String message) {
            super(message);
        }
    }
}
