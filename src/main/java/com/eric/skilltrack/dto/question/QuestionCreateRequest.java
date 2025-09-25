package com.eric.skilltrack.dto.question;

import jakarta.validation.constraints.NotBlank;

public record QuestionCreateRequest(
        @NotBlank String textoPergunta,
        String alternativaA,
        String alternativaB,
        String alternativaC,
        String alternativaD,
        String respostaCorreta,
        String categoria,
        String setorFoco,
        String linkImagem
) {}
