package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class Onboarding {
    private String idTurma;
    private String turno;
    private String idMultiplicador;
    private String idMultiplicadorReserva;
    private String dataInicio;
    private String dataFim;
    private String status;
}
