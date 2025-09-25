package com.eric.skilltrack.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record OnboardingCreateRequest(
        @NotBlank String idMultiplicador,
        String idMultiplicadorReserva,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio
) {}
