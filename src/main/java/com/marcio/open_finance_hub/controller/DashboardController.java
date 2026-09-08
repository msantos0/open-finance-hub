package com.marcio.open_finance_hub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcio.open_finance_hub.dto.DashboardSummaryResponseDTO;
import com.marcio.open_finance_hub.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get financial dashboard summary", description = "Returns global metrics or metrics for a specific month")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Financial summary returned"),
            @ApiResponse(responseCode = "400", description = "Month and year must be valid and provided together")
    })
    public ResponseEntity<DashboardSummaryResponseDTO> getSummary(
            @Parameter(description = "Month from 1 to 12", example = "9")
            @RequestParam(required = false) Integer month,
            @Parameter(description = "Year greater than 2000", example = "2026")
            @RequestParam(required = false) Integer year) {
        if ((month == null) != (year == null)) {
            throw new IllegalArgumentException("Month and year must be provided together");
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year != null && year <= 2000) {
            throw new IllegalArgumentException("Year must be greater than 2000");
        }

        return ResponseEntity.ok(dashboardService.getSummary(month, year));
    }
}