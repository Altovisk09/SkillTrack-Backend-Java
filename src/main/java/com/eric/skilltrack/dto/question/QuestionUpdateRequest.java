package com.eric.skilltrack.dto.question;

public record QuestionUpdateRequest(
        String textoPergunta,
        String alternativaA,
        String alternativaB,
        String alternativaC,
        String alternativaD,
        String respostaCorreta,
        String categoria,
        String setorFoco,
        String linkImagem
) {}
