package com.payforge.repository;

import com.payforge.entity.WebhookEvent;
import com.payforge.entity.WebhookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, Long> {

    Optional<WebhookEvent> findByEventId(
            String eventId
    );

    List<WebhookEvent> findByMerchantIdOrderByCreatedAtDesc(
            Long merchantId
    );

    Page<WebhookEvent> findByMerchantIdOrderByCreatedAtDesc(
            Long merchantId,
            Pageable pageable
    );

    Page<WebhookEvent> findByStatusOrderByCreatedAtAsc(
            WebhookStatus status,
            Pageable pageable
    );
}