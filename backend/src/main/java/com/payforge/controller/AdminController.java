package com.payforge.controller;

import com.payforge.dto.response.AdminStatsResponse;
import com.payforge.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse getStats() {

        return adminService.getStats();
    }
}