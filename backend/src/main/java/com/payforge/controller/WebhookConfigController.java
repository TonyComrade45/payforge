package com.payforge.controller;

import com.payforge.dto.request.WebhookConfigRequest;
import com.payforge.dto.response.WebhookConfigResponse;
import com.payforge.entity.User;
import com.payforge.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/webhook")
public class WebhookConfigController {

    private final WebhookService webhookService;

    public WebhookConfigController(
            WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PutMapping
    public WebhookConfigResponse configureWebhook(
            @AuthenticationPrincipal User merchant,
            @Valid @RequestBody WebhookConfigRequest request) {

        return webhookService.configureWebhook(
                merchant,
                request);
    }
}