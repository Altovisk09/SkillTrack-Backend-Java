package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.User;
import com.eric.skilltrack.repository.McgRepository; // interface
import com.eric.skilltrack.service.McgService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class McgServiceImpl implements McgService {
    private final McgRepository mcgRepository;

    public McgServiceImpl(McgRepository mcgRepository){
        this.mcgRepository = mcgRepository;
    }

    @Override
    public void registerTrainer(User user) throws IOException {
        // regra idempotente: evita duplicar por LDAP
        if (!mcgRepository.existsByLdap(user.getLdap())) {
            mcgRepository.insertBasicTrainerData(user);
        }
    }
}
