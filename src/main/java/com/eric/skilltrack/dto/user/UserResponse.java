package com.eric.skilltrack.dto.user;

import com.eric.skilltrack.model.enums.UserRole;

public record UserResponse(
        String ldap,
        String dataCadastro,
        UserRole role,
        String ultimaSessao,
        String nome,
        String cargo,
        String escala,
        String turno,
        String status,
        String empresa,
        String area,
        String processo,
        String gestorImediato,
        String gestor2,
        String gestor3,
        String admissao
) {}
