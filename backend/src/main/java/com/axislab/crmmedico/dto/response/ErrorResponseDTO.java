package com.axislab.crmmedico.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponseDTO(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
