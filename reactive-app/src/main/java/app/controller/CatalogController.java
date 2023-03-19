package app.controller;

import app.dao.CatalogDao;
import app.model.Currency;
import app.model.Product;
import app.model.User;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class CatalogController {
    private final CatalogDao catalogDao;

    public CatalogController(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    public Observable<String> process(HttpServerRequest<?> request) {
        String path = request.getDecodedPath().substring(1);
        Map<String, List<String>> params = request.getQueryParameters();
        int userId = parseUserId(params);
        switch (path) {
            case "users/register" -> {
                Currency currency = parseCurrency(params);
                return catalogDao.getUser(userId)
                        .flatMap(user -> {
                            if (user != null) {
                                return Observable.just(
                                        String.format("User with id %d already exists", userId)
                                );
                            }
                            return catalogDao.addUser(new User(userId, currency))
                                    .map(success ->
                                            String.format("User with id %d successfully registered", userId)
                                    );
                        });
            }
            case "users/info" -> {
                return catalogDao.getUser(userId)
                        .flatMap(user -> {
                            String info;
                            if (user == null) {
                                info = String.format("User with id %d does not exist", userId);
                            } else {
                                info = user.toString();
                            }
                            return Observable.just(info);
                        });
            }
            case "products" -> {
                return catalogDao.getUser(userId)
                        .flatMap(user -> {
                            if (user == null) {
                                return Observable.just(
                                        String.format("User with id %d does not exist", userId)
                                );
                            }
                            return catalogDao.getProducts()
                                    .map(product ->
                                            new Product(
                                                    product.getName(),
                                                    product.getPriceIn(user.getCurrency()),
                                                    user.getCurrency()
                                                ).toString()
                                    )
                                    .reduce(String::concat);
                        });
            }
            case "products/add" -> {
                String productName = parseString(params, "name");
                int productPrice = parseInt(params, "price");
                return catalogDao.getUser(userId)
                        .flatMap(user -> {
                            if (user == null) {
                                return Observable.just(
                                        String.format("User with id %d does not exist", userId)
                                );
                            }
                            return catalogDao.addProduct(
                                    new Product(
                                            productName,
                                            productPrice,
                                            user.getCurrency()
                                    )
                            )
                            .map(success ->
                                    String.format("%s successfully added", productName)
                            );
                        });
            }
            default -> {
                return Observable.just("Invalid request path");
            }
        }
    }

    private int parseUserId(Map<String, List<String>> params) {
        return parseInt(params,"userId");
    }

    private Currency parseCurrency(Map<String, List<String>> params) {
        return Currency.forName(parseString(params,"currency"));
    }

    private int parseInt(Map<String, List<String>> params, String parameter) {
        return Integer.parseInt(parseString(params, parameter));
    }

    private String parseString(Map<String, List<String>> params, String parameter) {
        return params.get(parameter).get(0);
    }
}
