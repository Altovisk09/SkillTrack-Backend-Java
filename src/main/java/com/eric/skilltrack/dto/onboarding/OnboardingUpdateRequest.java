package com.eric.skilltrack.dto.onboarding;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record OnboardingUpdateRequest(
        String idMultiplicador,
        String idMultiplicadorReserva,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        String status
) {}
