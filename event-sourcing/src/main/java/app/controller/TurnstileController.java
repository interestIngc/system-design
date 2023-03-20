package app.controller;

import app.service.TurnstileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static app.service.TurnstileService.TurnstileServiceException;

@RestController
@RequestMapping("turnstile")
public class TurnstileController {
    private final TurnstileService turnstileService;

    @Autowired
    public TurnstileController(TurnstileService turnstileService) {
        this.turnstileService = turnstileService;
    }

    @PostMapping(value = "/enter")
    public String onEnter(
            @RequestParam("id") int subscriptionId,
            @RequestParam("timestamp")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime timestamp) {
        try {
            turnstileService.letIn(subscriptionId, timestamp);
            return String.format("Client successfully entered with subscription %d", subscriptionId);
        } catch (TurnstileServiceException e) {
            return e.getMessage();
        }
    }

    @PostMapping(value = "/exit")
    public String onExit(
            @RequestParam("id") int subscriptionId,
            @RequestParam("timestamp")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime timestamp) {
        try {
            turnstileService.letOut(subscriptionId, timestamp);
            return String.format("Client successfully exited with subscription %d", subscriptionId);
        } catch (TurnstileServiceException e) {
            return e.getMessage();
        }
    }
}
