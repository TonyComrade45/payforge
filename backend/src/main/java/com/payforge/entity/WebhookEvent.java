package com.payforge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "webhook_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "event_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private String eventId;

    @Column(
            name = "event_type",
            nullable = false
    )
    private String eventType;

    @Column(
            name = "payment_reference_id"
    )
    private String paymentReferenceId;

    @Column(
            name = "refund_reference_id"
    )
    private String refundReferenceId;

    @Column(
            name = "merchant_id",
            nullable = false
    )
    private Long merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;


    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
}