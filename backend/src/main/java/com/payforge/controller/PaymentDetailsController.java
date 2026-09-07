package com.payforge.controller;

import com.payforge.dto.response.PaymentDetailsResponse;
import com.payforge.entity.User;
import com.payforge.service.PaymentDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentDetailsController {

    private final PaymentDetailsService paymentDetailsService;

    public PaymentDetailsController(
            PaymentDetailsService paymentDetailsService) {

        this.paymentDetailsService =
                paymentDetailsService;
    }

    @GetMapping("/{paymentReferenceId}")
    public PaymentDetailsResponse getPaymentDetails(
            @AuthenticationPrincipal User customer,
            @PathVariable String paymentReferenceId) {

        return paymentDetailsService.getPaymentDetails(
                customer,
                paymentReferenceId
        );
    }
}