package com.eric.skilltrack.dto.answer;

public record AnswerResponse(
        String idSessao,
        String idFuncionario,
        String nome,
        String idPergunta,
        String alternativaSelecionada,
        String respostaCorreta,
        String categoriaPergunta,
        String setorFoco,
        Long tempoResposta
) {}
