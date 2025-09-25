package com.eric.skilltrack.dto.foursteps;

import jakarta.validation.constraints.NotBlank;

public record FourStepsCreateRequest(
        @NotBlank String idMultiplicador,
        String ldapRep,
        String idTurma,
        String idSessao,
        String turno,
        String status,
        String statusLiveTest,
        String dataInicio,   // se quiser, troque para LocalDate e converta no service
        String dataFim
) {}
