package com.eric.skilltrack.dto.session;

import jakarta.validation.constraints.NotBlank;

public record SessionCreateRequest(
        @NotBlank String idTurma,
        @NotBlank String idSessao,       // opcional se gerado no backend
        String idMultiplicador,
        @NotBlank String nomeSessao,
        String idTemplate,
        String categoria,
        String setorFoco
) {}
