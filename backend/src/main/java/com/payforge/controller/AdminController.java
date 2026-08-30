package com.payforge.controller;

import com.payforge.dto.response.AdminStatsResponse;
import com.payforge.dto.response.AdminUserDetailsResponse;
import com.payforge.dto.response.AdminUserResponse;
import com.payforge.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @GetMapping("/users")
    public Page<AdminUserResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return adminService.getUsers(pageable);
    }
    @GetMapping("/users/{id}")
    public AdminUserDetailsResponse getUserDetails(
            @PathVariable Long id) {

        return adminService.getUserDetails(id);
    }
}