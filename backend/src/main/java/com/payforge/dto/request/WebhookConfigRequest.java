package com.payforge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WebhookConfigRequest {

    @NotBlank(message = "Webhook URL is required")
    @Pattern(
            regexp = "^https://.*",
            message = "Webhook URL must use HTTPS"
    )
    private String webhookUrl;
}