package com.eric.skilltrack.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerCreateRequest(
        @NotBlank String idSessao,
        @NotBlank String idFuncionario,
        String nome,
        @NotBlank String idPergunta,
        @NotBlank String alternativaSelecionada,
        Long tempoResposta // ms (opcional)
) {}
