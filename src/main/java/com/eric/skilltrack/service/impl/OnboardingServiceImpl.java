package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.repository.OnboardingRepository;
import com.eric.skilltrack.service.OnboardingService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingRepository onboardingRepository;

    public OnboardingServiceImpl(OnboardingRepository onboardingRepository) {
        this.onboardingRepository = onboardingRepository;
    }

    @Override
    public Optional<Onboarding> findById(String id) throws IOException {
        return onboardingRepository.findById(id);
    }

    @Override
    public List<Onboarding> findAll() throws IOException {
        return onboardingRepository.findAll();
    }

    @Override
    public Onboarding save(Onboarding onboarding) throws IOException {
        return onboardingRepository.save(onboarding);
    }

    @Override
    public Onboarding update(Onboarding onboarding) throws IOException {
        return onboardingRepository.update(onboarding);
    }

    @Override
    public void deleteById(String id) throws IOException {
        onboardingRepository.deleteById(id);
    }

    @Override
    public Onboarding createTurma(String idMultiplicador,
                                  String idMultiplicadorReserva,
                                  LocalDate dataInicio) throws IOException {
        return onboardingRepository.createTurma(idMultiplicador, idMultiplicadorReserva, dataInicio);
    }

    @Override
    public void setMultiplicadorReserva(String idTurma,
                                        String idMultiplicadorReserva) throws IOException {
        onboardingRepository.setMultiplicadorReserva(idTurma, idMultiplicadorReserva);
    }
}
