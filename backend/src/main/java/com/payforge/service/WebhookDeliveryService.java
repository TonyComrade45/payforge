package com.payforge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payforge.dto.response.WebhookPayload;
import com.payforge.entity.User;
import com.payforge.entity.WebhookEvent;
import com.payforge.entity.WebhookStatus;
import com.payforge.exception.ResourceNotFoundException;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class WebhookDeliveryService {

    private final WebhookEventRepository webhookEventRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public WebhookDeliveryService(
            WebhookEventRepository webhookEventRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            RestClient.Builder restClientBuilder) {

        this.webhookEventRepository = webhookEventRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.build();
    }

    @Transactional
    public void deliverWebhook(Long webhookEventId) {

        // 1. Find webhook event
        WebhookEvent event = webhookEventRepository
                .findById(webhookEventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook event not found"
                        ));

        // 2. Find merchant
        User merchant = userRepository
                .findById(event.getMerchantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found"
                        ));

        // 3. Check webhook configuration
        if (merchant.getWebhookUrl() == null ||
                merchant.getWebhookUrl().isBlank()) {

            throw new ResourceNotFoundException(
                    "Merchant webhook URL is not configured"
            );
        }

        // 4. Build external webhook payload
        WebhookPayload payload = new WebhookPayload(
                event.getEventId(),
                event.getEventType(),
                event.getPaymentReferenceId(),
                event.getRefundReferenceId(),
                event.getAmount(),
                event.getCreatedAt()
        );

        try {

            // 5. Send HTTP POST request
            restClient.post()
                    .uri(merchant.getWebhookUrl())
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            // 6. Mark delivery successful
            event.setStatus(WebhookStatus.SUCCESS);
            event.setAttempts(event.getAttempts() + 1);
            event.setDeliveredAt(
                    java.time.LocalDateTime.now()
            );

            webhookEventRepository.save(event);

        } catch (Exception exception) {

            // 7. Mark delivery failed
            event.setStatus(WebhookStatus.FAILED);
            event.setAttempts(event.getAttempts() + 1);

            webhookEventRepository.save(event);

            throw exception;
        }
    }
}