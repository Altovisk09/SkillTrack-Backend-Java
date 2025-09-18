package com.eric.skilltrack.model;

import lombok.Data;

@Data
public class Question {
    private String idPergunta;
    private String textoPergunta;
    private String alternativaA;
    private String alternativaB;
    private String alternativaC;
    private String alternativaD;
    private String respostaCorreta;
    private String categoria;
    private String setorFoco;
    private String linkImagem;
}
