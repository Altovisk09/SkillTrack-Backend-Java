package com.eric.skilltrack.dto.user;

import com.eric.skilltrack.model.enums.UserRole;

public record UserUpdateRequest(
        UserRole role
) {}
