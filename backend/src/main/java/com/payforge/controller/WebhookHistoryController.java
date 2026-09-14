package com.payforge.controller;

import com.payforge.dto.response.WebhookHistoryResponse;
import com.payforge.entity.User;
import com.payforge.service.WebhookHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/webhooks")
public class WebhookHistoryController {

    private final WebhookHistoryService webhookHistoryService;

    public WebhookHistoryController(
            WebhookHistoryService webhookHistoryService) {

        this.webhookHistoryService = webhookHistoryService;
    }

    @GetMapping
    public Page<WebhookHistoryResponse> getWebhookHistory(
            @AuthenticationPrincipal User merchant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return webhookHistoryService.getWebhookHistory(
                merchant,
                pageable
        );
    }
}