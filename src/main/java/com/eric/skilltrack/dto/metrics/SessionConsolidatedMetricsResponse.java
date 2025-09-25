package com.eric.skilltrack.dto.metrics;

public record SessionConsolidatedMetricsResponse(
        String idSessao,
        String idTurma,
        String idTemplate,
        String nomeTreinamento,
        Integer totalRespostas,
        Integer acertos,
        Double percentualAcertos,
        String dificuldadesIdentificadas,
        String multiplicadorResponsavel,
        String dataSessao
) {}
