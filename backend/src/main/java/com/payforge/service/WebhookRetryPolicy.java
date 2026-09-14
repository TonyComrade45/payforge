package com.payforge.service;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WebhookRetryPolicy {

    public Duration getRetryDelay(int attempts) {

        return switch (attempts) {
            case 1 -> Duration.ofSeconds(30);
            case 2 -> Duration.ofMinutes(1);
            case 3 -> Duration.ofMinutes(5);
            case 4 -> Duration.ofMinutes(15);
            default -> Duration.ZERO;
        };
    }
}