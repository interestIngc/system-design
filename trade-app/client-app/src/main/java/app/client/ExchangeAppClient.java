package app.client;

import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class ExchangeAppClient {
    private static final String EXCHANGE_APP_HOST = "http://localhost:8080/exchange";

    private final RestTemplate restTemplate;

    public ExchangeAppClient() {
        ClientHttpRequestFactory requestFactory = new
                HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        restTemplate = new RestTemplate(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(EXCHANGE_APP_HOST));
    }

    public int getStockPrice(String company) throws ExchangeAppClientException {
        Integer body = restTemplate.getForEntity(
                String.format("/%s/price", company),
                        Integer.class
                ).getBody();
        if (body == null) {
            throw new ExchangeAppClientException();
        }
        return body;
    }

    public void buyStocks(String company, int stockCount)
            throws ExchangeAppClientException {
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
                String.format("/%s/buy?count=%d", company, stockCount),
                null,
                Void.class
        );
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ExchangeAppClientException();
        }
    }

    public void sellStocks(String company, int stockCount)
            throws ExchangeAppClientException {
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
                String.format("/%s/add?count=%d", company, stockCount),
                null,
                Void.class
        );
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ExchangeAppClientException();
        }
    }

    public static class ExchangeAppClientException extends Exception {}
}
