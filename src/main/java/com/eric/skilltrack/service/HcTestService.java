package com.eric.skilltrack.service;

import com.eric.skilltrack.model.HC;
import com.eric.skilltrack.repository.HcRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class HcTestService {

    private final HcRepository hcRepository;

    public HcTestService(HcRepository hcRepository) {
        this.hcRepository = hcRepository;
    }

    /**
     * Testa a conexão com a planilha, buscando o LDAP e retornando
     * índice da linha se encontrado ou -1 se não existir.
     */
    public String testConnection(String ldap) {
        try {
            Optional<HC> hc = hcRepository.findByLdap(ldap);
            if (hc.isPresent()) {
                return "Encontrado: " + hc.get().getNome();
            } else {
                return "LDAP não encontrado";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao acessar a planilha: " + e.getMessage();
        }
    }
}
