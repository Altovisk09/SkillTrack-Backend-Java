// dto/participant/ParticipantCreateRequest.java
package com.eric.skilltrack.dto.participant;

import jakarta.validation.constraints.NotBlank;

public record ParticipantCreateRequest(
        @NotBlank String idTurma,
        String idPassos,
        @NotBlank String ldap,
        String nome,
        String role
) {}
