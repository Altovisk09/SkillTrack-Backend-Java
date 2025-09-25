package com.eric.skilltrack.dto.foursteps;

public record FourStepsResponse(
        String idPassos,
        String idMultiplicador,
        String ldapRep,
        String idTurma,
        String idSessao,
        String turno,
        String dataInicio,
        String dataFim,
        String status,
        String statusLiveTest
) {}
