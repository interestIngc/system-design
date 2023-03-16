package app.service;

import app.client.ExchangeAppClient;
import app.model.account.Account;
import app.model.response.AccountInfo;
import app.model.response.StockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

import static app.client.ExchangeAppClient.ExchangeAppClientException;

@Service
public class AccountService {
    private final MongoTemplate mongoTemplate;
    private final ExchangeAppClient exchangeAppClient;

    @Autowired
    public AccountService(MongoTemplate mongoTemplate, ExchangeAppClient exchangeAppClient) {
        this.mongoTemplate = mongoTemplate;
        this.exchangeAppClient = exchangeAppClient;
    }

    public void createUser(int id) throws AccountServiceException {
        if (findAccount(id) != null) {
            throw new AccountServiceException(
                    String.format("Account with id %d already exists", id));
        }

        mongoTemplate.insert(new Account(id));
    }

    public void addMoney(int id, int amount) throws AccountServiceException {
        Account account = getAccount(id);
        account.addAmount(amount);
        mongoTemplate.save(account);
    }

    public void buy(int id, String company, int stockCount)
            throws AccountServiceException {
        Account account = getAccount(id);

        try {
            int price = exchangeAppClient.getStockPrice(company);

            int total = price * stockCount;
            if (account.getAmount() < total) {
                throw new AccountServiceException(
                        String.format(
                                "Not enough money %d to buy %d stocks of %s with price %d",
                                account.getAmount(), stockCount, company, price)
                );
            }
            exchangeAppClient.buyStocks(company, stockCount);

            account.addStocks(company, stockCount);
            account.subtractAmount(total);
            mongoTemplate.save(account);
        } catch (ExchangeAppClientException e) {
            throw new AccountServiceException(
                    String.format("Could not buy %d stocks of %s", stockCount, company));
        }
    }

    public void sell(int id, String company, int stockCount) throws AccountServiceException {
        Account account = getAccount(id);

        try {
            int price = exchangeAppClient.getStockPrice(company);

            int stocksAvailable = account.getStocks(company);
            if (stocksAvailable < stockCount) {
                throw new AccountServiceException(
                        String.format("Cannot sell %d stocks, %d available", stockCount, stocksAvailable));
            }
            exchangeAppClient.sellStocks(company, stockCount);

            account.addStocks(company, -stockCount);
            account.addAmount(price * stockCount);
            mongoTemplate.save(account);
        } catch (ExchangeAppClientException e) {
            throw new AccountServiceException(
                    String.format("Could not sell %d stocks of %s", stockCount, company));
        }
    }

    public AccountInfo getAccountInfo(int id) throws AccountServiceException {
        Account account = getAccount(id);

        AccountInfo accountInfo = new AccountInfo(id);
        accountInfo.setAmount(account.getAmount());

        for (Map.Entry<String, Integer> entry : account.getCompanyStocks().entrySet()) {
            String company = entry.getKey();
            int count = entry.getValue();
            try {
                int price = exchangeAppClient.getStockPrice(company);
                accountInfo.addStockInfo(new StockInfo(company, count, price));
            } catch (ExchangeAppClientException e) {
                throw new AccountServiceException("Error while fetching data happened: " + e.getMessage());
            }
        }

        return accountInfo;
    }

    public int getAccountTotalAmount(int id) throws AccountServiceException {
        AccountInfo accountInfo = getAccountInfo(id);
        int total = accountInfo.getAmount();
        for (StockInfo stockInfo : accountInfo.getStockInfos()) {
            total += stockInfo.getPrice() * stockInfo.getCount();
        }
        return total;
    }

    private Account findAccount(int id) {
        Query query = new Query().addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Account.class);
    }

    private Account getAccount(int id) throws AccountServiceException {
        Account account = findAccount(id);

        if (account == null) {
            throw new AccountServiceException("Account does not exist");
        }

        return account;
    }

    public static class AccountServiceException extends Exception {
        public AccountServiceException(String message) {
            super(message);
        }
    }
}
