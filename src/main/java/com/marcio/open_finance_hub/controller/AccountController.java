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

import com.marcio.open_finance_hub.dto.AccountRequestDTO;
import com.marcio.open_finance_hub.dto.AccountResponseDTO;
import com.marcio.open_finance_hub.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountRequestDTO request) {
        AccountResponseDTO response = accountService.create(request);
        return ResponseEntity.created(URI.create("/api/accounts/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<AccountResponseDTO>> findActive() {
        return ResponseEntity.ok(accountService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody AccountRequestDTO request) {
        return ResponseEntity.ok(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}