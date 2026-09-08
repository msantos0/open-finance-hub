package com.marcio.open_finance_hub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcio.open_finance_hub.dto.DashboardSummaryResponseDTO;
import com.marcio.open_finance_hub.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Financial dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get financial dashboard summary")
    @ApiResponse(responseCode = "200", description = "Financial summary returned")
    public ResponseEntity<DashboardSummaryResponseDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}