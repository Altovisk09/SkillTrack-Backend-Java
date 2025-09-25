package com.eric.skilltrack.dto.session;

public record SessionUpdateRequest(
        String nomeSessao,
        String idTemplate,
        String categoria,
        String setorFoco
) {}
