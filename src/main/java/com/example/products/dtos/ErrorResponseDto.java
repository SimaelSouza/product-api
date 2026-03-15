package com.example.products.dtos;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {}
