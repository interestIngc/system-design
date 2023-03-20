package app.controller;

import app.model.response.admin.SubscriptionInfo;
import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;

import static app.service.AdminService.AdminServiceException;

@RestController
@RequestMapping("admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(value = "/create")
    public String createSubscription(
            @RequestParam int id,
            @RequestParam("expiry-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        try {
            adminService.createSubscription(id, expiryDate.plusDays(1).atStartOfDay());
            return String.format("subscription with id %d successfully created", id);
        } catch (AdminServiceException e) {
            return e.getMessage();
        }
    }

    @PostMapping(value = "/extend")
    public String extendSubscription(@RequestParam int id, @RequestParam int days) {
        try {
            adminService.extendSubscription(id, Duration.ofDays(days));
            return String.format("subscription with id %d successfully extended for %d days", id, days);
        } catch (AdminServiceException e) {
            return e.getMessage();
        }
    }

    @GetMapping(value = "/info")
    public String getSubscriptionInfo(@RequestParam int id) {
        try {
            SubscriptionInfo info = adminService.getSubscriptionInfo(id);
            return String.format(
                    "subscription with id %d expires on %s",
                    info.subscriptionId(),
                    info.expiryTime().toString());
        } catch (AdminServiceException e) {
            return e.getMessage();
        }
    }
}
