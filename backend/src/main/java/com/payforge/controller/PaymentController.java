package com.payforge.controller;

import com.payforge.dto.request.MerchantPaymentRequest;
import com.payforge.dto.response.PaymentResponse;
import com.payforge.entity.User;
import com.payforge.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/merchant")
    public PaymentResponse payMerchant(
            @AuthenticationPrincipal User customer,
            @Valid @RequestBody MerchantPaymentRequest request) {

        return paymentService.payMerchant(
                customer,
                request
        );
    }
}