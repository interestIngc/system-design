package app.controller;

import app.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static app.service.ExchangeService.StockServiceException;

@RestController
@RequestMapping("exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PutMapping("/{name}/create")
    public void createCompany(
            @PathVariable("name") String companyName,
            @RequestParam int price,
            @RequestParam int count) {
        exchangeService.createCompany(companyName, price, count);
    }

    @PostMapping("/{name}/add")
    public void addStocks(@PathVariable("name") String companyName, @RequestParam int count) {
        try {
            exchangeService.addStocks(companyName, count);
        } catch (StockServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{name}/price")
    public int getStockPrice(@PathVariable("name") String companyName) {
        try {
            return exchangeService.getStockPrice(companyName);
        } catch (StockServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{name}/count")
    public int getStockCount(@PathVariable("name") String companyName) {
        try {
            return exchangeService.getStockCount(companyName);
        } catch (StockServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{name}/buy")
    public void buyStocks(
            @PathVariable("name") String companyName,
            @RequestParam int count) {
        try {
            exchangeService.buyStocks(companyName, count);
        } catch (StockServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{name}/change")
    public void changeStockPrice(
            @PathVariable("name") String companyName,
            @RequestParam int price) {
        try {
            exchangeService.changeStockPrice(companyName, price);
        } catch (StockServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
