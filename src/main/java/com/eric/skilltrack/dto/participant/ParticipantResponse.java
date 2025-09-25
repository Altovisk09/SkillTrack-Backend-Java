package com.eric.skilltrack.dto.participant;

public record ParticipantResponse(
        String idTurma,
        String idPassos,
        String ldap,
        String nome,
        String role
) {}
