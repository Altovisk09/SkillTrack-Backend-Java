package com.eric.skilltrack.dto.onboarding;

public record OnboardingResponse(
        String idTurma,
        String turno,
        String idMultiplicador,
        String idMultiplicadorReserva,
        String dataInicio,   // dd/MM/yyyy (como vem do Sheets)
        String dataFim,
        String status
) {}
