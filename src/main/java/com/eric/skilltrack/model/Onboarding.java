package com.eric.skilltrack.model;

import com.eric.skilltrack.model.enums.TrainingType;
import lombok.Data;

@Data
public class Onboarding {
    private String idTurma;
    private String turno;
    private String idMultiplicador;
    private String dataInicio;
    private String dataFim;
    private TrainingType tipo;
    private String status;
}
