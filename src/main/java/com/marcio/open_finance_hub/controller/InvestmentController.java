package com.marcio.open_finance_hub.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcio.open_finance_hub.dto.InvestmentRequestDTO;
import com.marcio.open_finance_hub.dto.InvestmentResponseDTO;
import com.marcio.open_finance_hub.service.InvestmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/investments")
@Tag(name = "Investments", description = "Investment management")
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    @Operation(summary = "Create an investment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Investment created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<InvestmentResponseDTO> create(@Valid @RequestBody InvestmentRequestDTO request) {
        InvestmentResponseDTO response = investmentService.create(request);
        return ResponseEntity.created(URI.create("/api/investments/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "List all investments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investments returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<List<InvestmentResponseDTO>> findAll() {
        return ResponseEntity.ok(investmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find an investment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investment returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<InvestmentResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(investmentService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an investment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investment updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<InvestmentResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody InvestmentRequestDTO request) {
        return ResponseEntity.ok(investmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an investment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Investment deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        investmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}