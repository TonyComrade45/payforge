package com.payforge.controller;

import com.payforge.dto.request.RefundRequest;
import com.payforge.dto.response.RefundHistoryResponse;
import com.payforge.dto.response.RefundResponse;
import com.payforge.entity.User;
import com.payforge.service.RefundHistoryService;
import com.payforge.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;
    private final RefundHistoryService refundHistoryService;

    public RefundController(
            RefundService refundService,
            RefundHistoryService refundHistoryService) {

        this.refundService = refundService;
        this.refundHistoryService = refundHistoryService;
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

    @GetMapping
    public Page<RefundHistoryResponse> getRefundHistory(
            @AuthenticationPrincipal User customer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return refundHistoryService.getRefundHistory(
                customer,
                pageable
        );
    }
}