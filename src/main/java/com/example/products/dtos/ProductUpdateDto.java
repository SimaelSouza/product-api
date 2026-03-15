package com.example.products.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateDto(
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres") String name,
        @Positive(message = "o valor deve ser maior que 0")@Digits(integer = 10, fraction = 2) BigDecimal price) {
}
