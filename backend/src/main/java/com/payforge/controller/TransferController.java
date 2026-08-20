package com.payforge.controller;

import com.payforge.dto.request.TransferRequest;
import com.payforge.entity.User;
import com.payforge.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<String> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication) {

        User sender = (User) authentication.getPrincipal();

        transferService.transfer(sender, request);

        return ResponseEntity.ok(
                "Transfer successful"
        );
    }
}