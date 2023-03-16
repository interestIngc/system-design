package app;

import app.controller.AccountController;
import app.model.account.Account;
import app.model.response.AccountInfo;
import app.model.response.StockInfo;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationTest {
    private static final String COMPANY = "company";
    private static final int STOCKS_AT_EXCHANGE = 5;
    private static final int CLIENT_STOCKS = 2;
    private static final int PRICE = 30;
    private static final int ACCOUNT_ID = 0;
    private static final int USER_INITIAL_AMOUNT = 100;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccountController accountController;

    @ClassRule
    public static DockerComposeContainer<?> dockerComposeContainer =
            new DockerComposeContainer<>(new File("../exchange-app/docker-compose.yml"))
                    .withExposedService(
                            "exchange",
                            8080,
                            Wait.forListeningPort()
                    ).withLocalCompose(true)
                    .withOptions("--compatibility");

    @Before
    public void setUp() throws IOException, InterruptedException {
        HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(
                        URI.create(
                                String.format(
                                        "http://localhost:8080/exchange/%s/create?price=%d&count=%d",
                                        COMPANY,
                                        PRICE,
                                        STOCKS_AT_EXCHANGE
                                )
                        )).PUT(HttpRequest.BodyPublishers.ofByteArray(new byte[]{}))
                        .build(),
                HttpResponse.BodyHandlers.ofByteArray()
        );

        mongoTemplate.remove(
                new Query().addCriteria(Criteria.where("id").is(ACCOUNT_ID)),
                Account.class);
    }

    @Test
    public void createUser_userCreated() {
        accountController.createUser(ACCOUNT_ID);

        Account account = findAccount();
        assertThat(account.getId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    public void addMoney_moneyAdded() {
        accountController.createUser(ACCOUNT_ID);
        accountController.addMoney(ACCOUNT_ID, USER_INITIAL_AMOUNT);

        Account account = findAccount();
        assertThat(account).isEqualTo(
                new Account(ACCOUNT_ID, USER_INITIAL_AMOUNT)
        );
    }

    @Test
    public void buy_twoStocks_clientBillingAccountWithdrawed() {
        mongoTemplate.save(new Account(ACCOUNT_ID, USER_INITIAL_AMOUNT));

        accountController.buy(ACCOUNT_ID, COMPANY, CLIENT_STOCKS);

        Account account = findAccount();
        assertThat(account.getAmount()).isEqualTo(40);
        assertThat(account.getCompanyStocks()).containsExactlyEntriesOf(
                Map.of(COMPANY, CLIENT_STOCKS)
        );
    }

    @Test
    public void sell_twoStocksSold_clientBillingAccountDebited()
            throws IOException, InterruptedException {
        mongoTemplate.save(
                new Account(
                        ACCOUNT_ID,
                        USER_INITIAL_AMOUNT,
                        Map.of(COMPANY, CLIENT_STOCKS)
                )
        );

        accountController.sell(ACCOUNT_ID, COMPANY, CLIENT_STOCKS);

        Account account = findAccount();
        assertThat(account.getAmount()).isEqualTo(USER_INITIAL_AMOUNT + CLIENT_STOCKS * PRICE);
        assertThat(account.getCompanyStocks()).containsExactlyEntriesOf(
                Map.of(COMPANY, 0)
        );
        HttpResponse<String> response =
                HttpClient.newHttpClient()
                        .send(
                                HttpRequest.newBuilder(
                                        URI.create(
                                                String.format(
                                                        "http://localhost:8080/exchange/%s/count",
                                                        COMPANY
                                                )
                                        )
                                ).GET().build(),
                                HttpResponse.BodyHandlers.ofString()
                        );
        int newStocksAtExchange = Integer.parseInt(response.body());
        assertThat(newStocksAtExchange).isEqualTo(STOCKS_AT_EXCHANGE + CLIENT_STOCKS);
    }

    @Test
    public void getAccountInfo_stocksInformationReturnedCorrectly() {
        mongoTemplate.save(
                new Account(
                        ACCOUNT_ID,
                        USER_INITIAL_AMOUNT,
                        Map.of(COMPANY, CLIENT_STOCKS)
                )
        );

        AccountInfo accountInfo = accountController.getAccountInfo(ACCOUNT_ID);

        assertThat(accountInfo).isEqualTo(
                new AccountInfo(
                        ACCOUNT_ID,
                        USER_INITIAL_AMOUNT,
                        List.of(
                                new StockInfo(COMPANY, CLIENT_STOCKS, PRICE)
                        )
                )
        );
    }

    @Test
    public void getAccountTotalAmount_amountCalculatedCorrectly() {
        mongoTemplate.save(
                new Account(
                        ACCOUNT_ID,
                        USER_INITIAL_AMOUNT,
                        Map.of(COMPANY, CLIENT_STOCKS)
                )
        );

        int total = accountController.getAccountTotalAmount(ACCOUNT_ID);

        assertThat(total).isEqualTo(USER_INITIAL_AMOUNT + CLIENT_STOCKS * PRICE);
    }

    private Account findAccount() {
        Query query = new Query().addCriteria(Criteria.where("id").is(ACCOUNT_ID));
        return mongoTemplate.findOne(query, Account.class);
    }
}
