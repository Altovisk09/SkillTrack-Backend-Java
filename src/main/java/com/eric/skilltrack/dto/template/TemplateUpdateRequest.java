package com.eric.skilltrack.dto.template;

import java.util.List;

public record TemplateUpdateRequest(
        String nomeTreinamento,
        String categoria,
        List<String> idsPerguntas,
        String setorFoco,
        String autor,
        String tipo
) {}
