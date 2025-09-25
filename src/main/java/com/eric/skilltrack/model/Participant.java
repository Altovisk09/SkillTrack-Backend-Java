package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class Participant {
    private String idTurma;
    private String IdPassos;
    private String ldap;
    private String nome;
    private String role;
}
