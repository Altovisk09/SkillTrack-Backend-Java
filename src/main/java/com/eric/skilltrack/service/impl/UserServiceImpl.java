package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.HC;
import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;
import com.eric.skilltrack.repository.HcRepository;
import com.eric.skilltrack.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final HcRepository hcRepository;

    public UserServiceImpl(UserRepository userRepository, HcRepository hcRepository) {
        this.userRepository = userRepository;
        this.hcRepository = hcRepository;
    }

    @Override
    public Optional<User> getByLdap(String ldap) throws IOException {
        return userRepository.findByLdap(ldap);
    }

    @Override
    public List<User> getAllUsers() throws IOException {
        return userRepository.findAllUsers();
    }

    @Override
    public User updateRole(String ldap, UserRole role) throws IOException {
        var userOpt = userRepository.findByLdap(ldap);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado: " + ldap);
        }
        User u = userOpt.get();
        u.setRole(role);
        return userRepository.update(u);
    }

    @Override
    public void updateLastSession(String ldap) throws IOException {
        String now = LocalDateTime.now().toString();
        userRepository.updateLastSession(ldap, now);
    }

    @Override
    @Scheduled(cron = "0 0 12 * * ?") // roda todo dia às 12:00
    public void syncFromHc() throws IOException {
        userRepository.syncFromHc(hcRepository);
    }

    /**
     * MÉTODO CORRIGIDO E COM LOGS
     */
    @Override
    public User registerUser(String ldap) throws IOException {
        System.out.println("--- INICIANDO PROCESSO DE REGISTRO PARA O LDAP: " + ldap + " ---");

        // 1. Verifica se o usuário JÁ EXISTE na planilha "Usuarios"
        System.out.println("[LOG 1] Verificando se o usuário já existe na base 'Usuarios'...");
        Optional<User> existingUser = userRepository.findByLdap(ldap);
        if (existingUser.isPresent()) {
            System.out.println("[LOG 2] SUCESSO: Usuário já existe na base. Retornando dados existentes.");
            System.out.println("--- FIM DO PROCESSO DE REGISTRO ---");
            return existingUser.get();
        }

        // 2. Se não existe, busca os dados completos na planilha "HC"
        System.out.println("[LOG 2] Usuário não encontrado. Buscando na base 'HC'...");
        Optional<HC> hcDataOpt = hcRepository.findByLdap(ldap);
        if (hcDataOpt.isEmpty()) {
            System.out.println("[LOG 3] ERRO: Usuário com LDAP '" + ldap + "' também não foi encontrado na base 'HC'.");
            System.out.println("--- FIM DO PROCESSO DE REGISTRO ---");
            throw new IllegalArgumentException("Usuário com LDAP '" + ldap + "' não encontrado na base de dados HC.");
        }

        // 3. Se encontrou no HC, cria um novo objeto User e preenche os dados
        System.out.println("[LOG 3] SUCESSO: Usuário encontrado no HC. Preparando para criar novo registro...");
        HC hcData = hcDataOpt.get();
        User newUser = new User();

        // Mapeamento dos dados de HC para User
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

        // 4. Salva o novo usuário (agora completo) na planilha "Usuarios"
        System.out.println("[LOG 4] Chamando userRepository.save() para salvar o novo usuário...");
        User savedUser = userRepository.save(newUser);
        System.out.println("[LOG 5] SUCESSO: Usuário salvo com sucesso!");
        System.out.println("--- FIM DO PROCESSO DE REGISTRO ---");
        return savedUser;
    }
}