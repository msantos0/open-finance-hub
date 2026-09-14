package com.marcio.open_finance_hub.dto;

public record AuthResponseDTO(
        String token,
        String tokenType,
        long expiresIn,
        String userId,
        String name,
        String email) {
}
