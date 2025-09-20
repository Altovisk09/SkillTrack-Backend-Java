package com.eric.skilltrack.model.enums;

public enum UserRole {
    PARTICIPANT,
    TRAINER,
    ADMIN;

    public static UserRole fromString(String value) {
        if (value == null) return PARTICIPANT; // default
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PARTICIPANT;
        }
    }
}
