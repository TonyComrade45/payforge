package com.payforge.service;

import com.payforge.entity.WebhookEvent;
import com.payforge.entity.WebhookStatus;
import com.payforge.repository.WebhookEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WebhookRetryService {

    private static final int MAX_ATTEMPTS = 5;

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookDeliveryService webhookDeliveryService;

    public WebhookRetryService(
            WebhookEventRepository webhookEventRepository,
            WebhookDeliveryService webhookDeliveryService) {

        this.webhookEventRepository = webhookEventRepository;
        this.webhookDeliveryService = webhookDeliveryService;
    }

    @Scheduled(fixedDelay = 30000)
    public void retryFailedWebhooks() {

        List<WebhookEvent> events =
                webhookEventRepository
                        .findTop100ByStatusAndNextRetryAtLessThanEqualOrderByNextRetryAtAsc(
                                WebhookStatus.FAILED,
                                LocalDateTime.now()
                        );

        for (WebhookEvent event : events) {

            if (event.getAttempts() >= MAX_ATTEMPTS) {
                continue;
            }

            webhookDeliveryService.deliverWebhook(
                    event.getId()
            );
        }
    }
}