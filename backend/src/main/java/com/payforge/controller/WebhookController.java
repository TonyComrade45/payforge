package com.payforge.controller;

import com.payforge.entity.WebhookEvent;
import com.payforge.service.WebhookService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(
            WebhookService webhookService) {

        this.webhookService = webhookService;
    }

    @PostMapping("/test/payment")
    public WebhookEvent createPaymentWebhook(
            @RequestParam Long merchantId,
            @RequestParam String paymentReferenceId,
            @RequestParam BigDecimal amount) {

        return webhookService.createPaymentWebhook(
                merchantId,
                paymentReferenceId,
                amount
        );
    }

    @PostMapping("/test/refund")
    public WebhookEvent createRefundWebhook(
            @RequestParam Long merchantId,
            @RequestParam String paymentReferenceId,
            @RequestParam String refundReferenceId,
            @RequestParam BigDecimal amount) {

        return webhookService.createRefundWebhook(
                merchantId,
                paymentReferenceId,
                refundReferenceId,
                amount
        );
    }
}