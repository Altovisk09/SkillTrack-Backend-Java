package com.eric.skilltrack.dto.mcg;

public record McgResponse(
        String idMultiplicador,
        String turno,
        String ldap,
        String nome,
        String atividadeAtual,
        String sessaoTurma,
        String dataInicio,
        String dataFim,
        String statusAtual
) {}
