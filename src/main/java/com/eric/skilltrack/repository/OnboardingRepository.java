package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.model.enums.TrainingType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OnboardingRepository extends BaseRepository<Onboarding, String> {
    @Override
    Optional<Onboarding> findById(String s) throws IOException;

    @Override
    List<Onboarding> findAll() throws IOException;

    @Override
    Onboarding save(Onboarding entity) throws IOException;

    @Override
    Onboarding update(Onboarding entity) throws IOException;

    @Override
    void deleteById(String s) throws IOException;

    @Override
    int findRowIndexByColumn(String sheetName, String columnName, String value) throws IOException;

    Onboarding createTurma(String idMultiplicador,
                           LocalDate dataInicio,
                           TrainingType tipo) throws IOException;
}