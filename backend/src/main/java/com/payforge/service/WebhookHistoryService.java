package com.payforge.service;

import com.payforge.dto.response.WebhookHistoryResponse;
import com.payforge.entity.User;
import com.payforge.entity.WebhookEvent;
import com.payforge.repository.WebhookEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WebhookHistoryService {

    private final WebhookEventRepository webhookEventRepository;

    public WebhookHistoryService(
            WebhookEventRepository webhookEventRepository) {

        this.webhookEventRepository = webhookEventRepository;
    }

    public Page<WebhookHistoryResponse> getWebhookHistory(
            User merchant,
            Pageable pageable) {

        Page<WebhookEvent> events =
                webhookEventRepository
                        .findByMerchantIdOrderByCreatedAtDesc(
                                merchant.getId(),
                                pageable
                        );

        return events.map(event ->
                new WebhookHistoryResponse(
                        event.getEventId(),
                        event.getEventType(),
                        event.getPaymentReferenceId(),
                        event.getRefundReferenceId(),
                        event.getAmount(),
                        event.getStatus(),
                        event.getAttempts(),
                        event.getCreatedAt(),
                        event.getDeliveredAt(),
                        event.getNextRetryAt()
                )
        );
    }
}