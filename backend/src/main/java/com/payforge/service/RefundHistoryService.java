package com.payforge.service;

import com.payforge.dto.response.RefundHistoryResponse;
import com.payforge.entity.Refund;
import com.payforge.entity.User;
import com.payforge.repository.RefundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RefundHistoryService {

    private final RefundRepository refundRepository;

    public RefundHistoryService(
            RefundRepository refundRepository) {

        this.refundRepository = refundRepository;
    }

    public Page<RefundHistoryResponse> getRefundHistory(
            User customer,
            Pageable pageable) {

        Page<Refund> refunds =
                refundRepository
                        .findByCustomerIdOrderByCreatedAtDesc(
                                customer.getId(),
                                pageable
                        );

        return refunds.map(refund ->
                new RefundHistoryResponse(
                        refund.getReferenceId(),
                        refund.getPaymentReferenceId(),
                        refund.getMerchant().getEmail(),
                        refund.getAmount(),
                        refund.getStatus().name(),
                        refund.getCreatedAt()
                )
        );
    }
}