package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class FourSteps {
    private String idPassos;
    private String idMultiplicador;
    private String ldapRep;
    private String idTurma;
    private String idSessao;
    private String turno;
    private String dataInicio;
    private String dataFim;
    private String status;
    private String statusLiveTest;
}
