package com.eric.skilltrack.dto.mcg;

import jakarta.validation.constraints.NotBlank;

public record McgCreateRequest(
        @NotBlank String idMultiplicador,
        String turno,
        @NotBlank String ldap,
        @NotBlank String nome
) {}
