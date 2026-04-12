package com.example.products.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank(message = "Email é obrigatório")
        @Size(max = 50)
        @Email(message = "Email inválido")
        String email,

        @NotBlank
        @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres", max = 100)
        String password,

        @NotBlank
        String confirmPassword
) {}
