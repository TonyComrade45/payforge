package com.payforge.controller;

import com.payforge.service.WebhookDeliveryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookDeliveryTestController {

    private final WebhookDeliveryService webhookDeliveryService;

    public WebhookDeliveryTestController(
            WebhookDeliveryService webhookDeliveryService) {

        this.webhookDeliveryService = webhookDeliveryService;
    }

    @PostMapping("/test/deliver/{webhookEventId}")
    public String deliverWebhook(
            @PathVariable Long webhookEventId) {

        webhookDeliveryService.deliverWebhook(
                webhookEventId);

        return "Webhook delivery attempted";
    }
}