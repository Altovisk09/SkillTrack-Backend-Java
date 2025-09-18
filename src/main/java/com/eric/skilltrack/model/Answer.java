package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class Answer {
    private String idSessao;
    private String idFuncionario;
    private String nome;
    private String idPergunta;
    private String alternativaSelecionada;
    private String respostaCorreta;
    private String categoriaPergunta;
    private String setorFoco;
    private Long tempoResposta; // ms
}
