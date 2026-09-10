package com.payforge.service;

import com.payforge.entity.WebhookEvent;
import com.payforge.entity.WebhookStatus;
import com.payforge.exception.BadRequestException;
import com.payforge.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payforge.dto.request.WebhookConfigRequest;
import com.payforge.dto.response.WebhookConfigResponse;
import com.payforge.entity.Role;
import com.payforge.entity.User;
import com.payforge.repository.UserRepository;
import java.util.UUID;

import java.util.UUID;

@Service
public class WebhookService {

    private final WebhookEventRepository webhookEventRepository;
    private final UserRepository userRepository;
    public WebhookService(
            WebhookEventRepository webhookEventRepository, UserRepository userRepository) {

        this.webhookEventRepository =
                webhookEventRepository;
        this.userRepository = userRepository;
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
    public WebhookConfigResponse configureWebhook(
            User merchant,
            WebhookConfigRequest request) {

        if (merchant.getRole() != Role.MERCHANT) {
            throw new BadRequestException(
                    "Only merchants can configure webhooks");
        }

        String webhookSecret =
                "whsec_" + UUID.randomUUID();

        merchant.setWebhookUrl(request.getWebhookUrl());
        merchant.setWebhookSecret(webhookSecret);

        userRepository.save(merchant);

        return new WebhookConfigResponse(
                merchant.getWebhookUrl(),
                merchant.getWebhookSecret()
        );
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