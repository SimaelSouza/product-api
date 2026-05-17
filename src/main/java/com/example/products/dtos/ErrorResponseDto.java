package com.example.products.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {}
