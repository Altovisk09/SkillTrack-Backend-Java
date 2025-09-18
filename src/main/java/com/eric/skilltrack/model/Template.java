package com.eric.skilltrack.model;

import lombok.Data;
import java.util.List;

@Data
public class Template {
    private String idTemplate;
    private String nomeTreinamento;
    private String categoria;
    private List<String> idsPerguntas;
    private String setorFoco;
    private String autor;
    private String tipo;
}
