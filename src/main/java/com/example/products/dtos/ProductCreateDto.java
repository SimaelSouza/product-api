package com.example.products.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDto(
        @NotBlank(message = "O nome do produto é obrigatório") @Size(max = 50, message = "O nome deve possuir até 50 caracteres") String name,
        @NotNull(message = "O valor não pode ser nulo")@Positive(message = "O valor deve ser maior que zero")@Digits(integer = 10, fraction = 2) BigDecimal price
) {}
