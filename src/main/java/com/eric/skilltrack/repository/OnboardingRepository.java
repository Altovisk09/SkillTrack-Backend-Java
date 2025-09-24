package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.Onboarding;

import java.io.IOException;
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
}