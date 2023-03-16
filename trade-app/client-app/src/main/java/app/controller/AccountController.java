package app.controller;

import app.model.response.AccountInfo;
import app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static app.service.AccountService.AccountServiceException;

@RestController
@RequestMapping("account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{id}/create")
    public void createUser(@PathVariable("id") int id) {
        try {
            accountService.createUser(id);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/add")
    public void addMoney(@PathVariable("id") int id, @RequestParam int amount) {
        try {
            accountService.addMoney(id, amount);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/buy")
    public void buy(
            @PathVariable("id") int id,
            @RequestParam String company,
            @RequestParam("count") int stockCount) {
        try {
            accountService.buy(id, company, stockCount);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/sell")
    public void sell(
            @PathVariable("id") int id,
            @RequestParam String company,
            @RequestParam("count") int stockCount) {
        try {
            accountService.sell(id, company, stockCount);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(value = "/{id}/info", produces = "application/json")
    public AccountInfo getAccountInfo(@PathVariable("id") int id) {
        try {
            return accountService.getAccountInfo(id);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}/sum")
    public int getAccountTotalAmount(@PathVariable("id") int id) {
        try {
            return accountService.getAccountTotalAmount(id);
        } catch (AccountServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
