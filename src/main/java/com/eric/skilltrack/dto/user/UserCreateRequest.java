package com.eric.skilltrack.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String ldap
) {}
