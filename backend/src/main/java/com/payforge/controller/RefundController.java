package com.payforge.controller;

import com.payforge.dto.request.RefundRequest;
import com.payforge.dto.response.RefundResponse;
import com.payforge.entity.User;
import com.payforge.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(
            RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public RefundResponse refund(
            @AuthenticationPrincipal User customer,
            @Valid @RequestBody RefundRequest request) {

        return refundService.refund(
                customer,
                request
        );
    }
}