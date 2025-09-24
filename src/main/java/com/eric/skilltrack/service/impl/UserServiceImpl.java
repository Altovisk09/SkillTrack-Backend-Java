package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.HC;
import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;
import com.eric.skilltrack.repository.HcRepository;
import com.eric.skilltrack.repository.UserRepository;
import com.eric.skilltrack.service.McgService;
import com.eric.skilltrack.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final McgService mcgService;         // interface
    private final UserRepository userRepository; // interface
    private final HcRepository hcRepository;     // interface

    public UserServiceImpl(
            McgService mcgService,
            UserRepository userRepository,
            HcRepository hcRepository
    ) {
        this.mcgService = mcgService;
        this.userRepository = userRepository;
        this.hcRepository = hcRepository;
    }

    @Override
    public Optional<User> getByLdap(String ldap) throws IOException {
        return userRepository.findByLdap(ldap);
    }

    @Override
    public User updateRole(String ldap, UserRole role) throws IOException {
        User u = userRepository.findByLdap(ldap)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + ldap));

        UserRole oldRole = u.getRole();
        u.setRole(role);

        User updatedUser = userRepository.update(u);

        // garante registro na MCG quando estiver como TRAINER (idempotente)
        if (role == UserRole.TRAINER && oldRole != UserRole.TRAINER) {
            mcgService.registerTrainer(updatedUser);
        }

        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() throws IOException {
        return userRepository.findAllUsers();
    }

    @Override
    public void updateLastSession(String ldap) throws IOException {
        userRepository.updateLastSession(ldap, LocalDateTime.now().toString());
    }

    @Override
    @Scheduled(cron = "0 0 12 * * ?", zone = "America/Sao_Paulo") // 12:00 BRT
    public void syncFromHc() throws IOException {
        userRepository.syncFromHc(hcRepository);
    }

    @Override
    public User registerUser(String ldap) throws IOException {
        Optional<User> existingUser = userRepository.findByLdap(ldap);
        if (existingUser.isPresent()) return existingUser.get();

        HC hcData = hcRepository.findByLdap(ldap)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário com LDAP '" + ldap + "' não encontrado na base de dados HC."
                ));

        User newUser = new User();
        newUser.setLdap(hcData.getLdap());
        newUser.setNome(hcData.getNome());
        newUser.setCargo(hcData.getCargo());
        newUser.setEscala(hcData.getEscala());
        newUser.setTurno(hcData.getTurnoRes());
        newUser.setStatus(hcData.getStatus());
        newUser.setEmpresa(hcData.getEmpresa());
        newUser.setArea(hcData.getAreaSOP());
        newUser.setProcesso(hcData.getProcesso());
        newUser.setGestorImediato(hcData.getGestorImediato());
        newUser.setGestor2(hcData.getGestao2());
        newUser.setGestor3(hcData.getGestao3());
        newUser.setAdmissao(hcData.getAdmissao());
        newUser.setDataCadastro(LocalDate.now().toString());
        newUser.setRole(UserRole.PARTICIPANT);
        newUser.setUltimaSessao("Sem dados");

        return userRepository.save(newUser);
    }
}
