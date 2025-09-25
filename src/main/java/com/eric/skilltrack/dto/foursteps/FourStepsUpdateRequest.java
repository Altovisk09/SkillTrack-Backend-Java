package com.eric.skilltrack.dto.foursteps;

public record FourStepsUpdateRequest(
        String ldapRep,
        String idTurma,
        String idSessao,
        String turno,
        String status,
        String statusLiveTest,
        String dataInicio,
        String dataFim
) {}
