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
import org.springframework.web.client.RestClient;

@Service
public class WebhookDeliveryService {

    private final WebhookEventRepository webhookEventRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final WebhookSignatureService webhookSignatureService;

    public WebhookDeliveryService(
            WebhookEventRepository webhookEventRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            RestClient.Builder restClientBuilder,
            WebhookSignatureService webhookSignatureService) {

        this.webhookEventRepository = webhookEventRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.build();
        this.webhookSignatureService = webhookSignatureService;
    }

    public void deliverWebhook(Long webhookEventId) {

        WebhookEvent event = webhookEventRepository
                .findById(webhookEventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook event not found"
                        ));

        User merchant = userRepository
                .findById(event.getMerchantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found"
                        ));

        if (merchant.getWebhookUrl() == null ||
                merchant.getWebhookUrl().isBlank()) {

            markFailed(event);
            return;
        }

        if (merchant.getWebhookSecret() == null ||
                merchant.getWebhookSecret().isBlank()) {

            markFailed(event);
            return;
        }

        WebhookPayload payload = new WebhookPayload(
                event.getEventId(),
                event.getEventType(),
                event.getPaymentReferenceId(),
                event.getRefundReferenceId(),
                event.getAmount(),
                event.getCreatedAt()
        );

        try {

            String payloadJson =
                    objectMapper.writeValueAsString(payload);

            String signature =
                    webhookSignatureService.generateSignature(
                            payloadJson,
                            merchant.getWebhookSecret()
                    );

            restClient.post()
                    .uri(merchant.getWebhookUrl())
                    .header(
                            "X-PayForge-Signature",
                            signature
                    )
                    .header(
                            "Content-Type",
                            "application/json"
                    )
                    .body(payloadJson)
                    .retrieve()
                    .toBodilessEntity();

            event.setStatus(WebhookStatus.SUCCESS);

            event.setAttempts(
                    event.getAttempts() + 1
            );

            event.setDeliveredAt(
                    java.time.LocalDateTime.now()
            );

            webhookEventRepository.save(event);

        } catch (Exception exception) {

            markFailed(event);
        }
    }

    private void markFailed(WebhookEvent event) {

        event.setStatus(WebhookStatus.FAILED);

        event.setAttempts(
                event.getAttempts() + 1
        );

        webhookEventRepository.save(event);
    }
}