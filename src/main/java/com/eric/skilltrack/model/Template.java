package com.eric.skilltrack.model;

import com.eric.skilltrack.model.enums.TrainingType;
import lombok.Data;
import java.util.List;

@Data
public class Template {
    private String idTemplate;
    private String nomeTreinamento;
    private String categoria;
    private String idsPerguntas;
    private String setorFoco;
    private String autor;
    private TrainingType tipo;
}
