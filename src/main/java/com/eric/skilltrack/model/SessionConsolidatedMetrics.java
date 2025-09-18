package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class SessionConsolidatedMetrics {
    private String idSessao;
    private String idTurma;
    private String idTemplate;
    private String nomeTreinamento;
    private Integer totalRespostas;
    private Integer acertos;
    private Double percentualAcertos;
    private String dificuldadesIdentificadas;
    private String multiplicadorResponsavel;
    private String dataSessao;
}
