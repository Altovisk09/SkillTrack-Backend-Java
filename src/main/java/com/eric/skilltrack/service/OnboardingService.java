package com.eric.skilltrack.service;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.model.enums.TrainingType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OnboardingService {
    Optional<Onboarding> findById(String id) throws IOException;
    List<Onboarding> findAll() throws IOException;
    Onboarding save(Onboarding onboarding) throws IOException;
    Onboarding update(Onboarding onboarding) throws IOException;
    void deleteById(String id) throws IOException;

    Onboarding createTurma(String idMultiplicador,
                           LocalDate dataInicio,
                           TrainingType tipo) throws IOException;

}
