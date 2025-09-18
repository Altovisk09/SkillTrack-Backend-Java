package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class User {
    private String ldap;
    private String dataCadastro;
    private String role;
    private String ultimaSessao;
    private String nome;
    private String cargo;
    private String escala;
    private String turno;
    private String status;
    private String empresa;
    private String area;
    private String processo;
    private String gestorImediato;
    private String gestor2;
    private String gestor3;
    private String admissao;
}
