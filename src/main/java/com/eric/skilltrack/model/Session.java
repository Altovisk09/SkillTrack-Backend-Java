package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class Session {
    private String idTurma;
    private String idSessao;
    private String idMultiplicador;
    private String nomeSessao;
    private String idTemplate;
    private String dataCriacao;
    private String categoria;
    private String setorFoco;
}
