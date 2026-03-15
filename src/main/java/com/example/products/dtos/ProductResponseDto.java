package com.example.products.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto (UUID id, String name, BigDecimal price) {
}
