package com.eric.skilltrack.dto.session;

public record SessionResponse(
        String idTurma,
        String idSessao,
        String idMultiplicador,
        String nomeSessao,
        String idTemplate,
        String dataCriacao,
        String categoria,
        String setorFoco
) {}
