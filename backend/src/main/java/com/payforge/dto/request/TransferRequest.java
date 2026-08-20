package com.payforge.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class TransferRequest {
    private String receiverEmail;
    private BigDecimal amount;
    
}
