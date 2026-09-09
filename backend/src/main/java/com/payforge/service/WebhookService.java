package com.payforge.service;

import com.payforge.entity.WebhookEvent;
import com.payforge.entity.WebhookStatus;
import com.payforge.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WebhookService {

    private final WebhookEventRepository webhookEventRepository;

    public WebhookService(
            WebhookEventRepository webhookEventRepository) {

        this.webhookEventRepository =
                webhookEventRepository;
    }

    @Transactional
    public WebhookEvent createPaymentWebhook(
            Long merchantId,
            String paymentReferenceId) {

        WebhookEvent event = new WebhookEvent();

        event.setEventId(
                "EVT-" + UUID.randomUUID()
        );

        event.setEventType(
                "PAYMENT_SUCCESS"
        );

        event.setPaymentReferenceId(
                paymentReferenceId
        );

        event.setMerchantId(
                merchantId
        );

        event.setStatus(
                WebhookStatus.PENDING
        );

        event.setAttempts(0);

        return webhookEventRepository.save(event);
    }

    @Transactional
    public WebhookEvent createRefundWebhook(
            Long merchantId,
            String paymentReferenceId,
            String refundReferenceId) {

        WebhookEvent event = new WebhookEvent();

        event.setEventId(
                "EVT-" + UUID.randomUUID()
        );

        event.setEventType(
                "REFUND_SUCCESS"
        );

        event.setPaymentReferenceId(
                paymentReferenceId
        );

        event.setRefundReferenceId(
                refundReferenceId
        );

        event.setMerchantId(
                merchantId
        );

        event.setStatus(
                WebhookStatus.PENDING
        );

        event.setAttempts(0);

        return webhookEventRepository.save(event);
    }
}