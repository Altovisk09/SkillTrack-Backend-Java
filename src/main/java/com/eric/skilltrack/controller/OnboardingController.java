package com.eric.skilltrack.controller;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.model.enums.TrainingType;
import com.eric.skilltrack.service.OnboardingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/turmas")
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/create",
            consumes = { "application/x-www-form-urlencoded", "multipart/form-data", "application/json" }
    )
    public ResponseEntity<Onboarding> createTurma(
            // FORM
            @RequestParam(value = "idMultiplicador", required = false) String idMultiplicadorParam,
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicioParam,
            @RequestParam(value = "tipo", required = false) String tipoParam,
            // JSON
            @RequestBody(required = false) CreateTurmaRequest body
    ) throws IOException {

        String idMultiplicador = coalesce(idMultiplicadorParam, body == null ? null : body.idMultiplicador());
        LocalDate dataInicio = (dataInicioParam != null) ? dataInicioParam : (body == null ? null : body.dataInicio());
        TrainingType tipo = parseTipo(coalesce(tipoParam, body == null ? null : body.tipo()));

        if (idMultiplicador == null || dataInicio == null) return ResponseEntity.badRequest().build();

        Onboarding created = service.createTurma(idMultiplicador, dataInicio, tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private String coalesce(String a, String b) { return (a != null && !a.isBlank()) ? a : b; }

    private TrainingType parseTipo(String s) {
        if (s == null || s.isBlank()) return TrainingType.ONBOARDING;
        try { return TrainingType.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return TrainingType.ONBOARDING; }
    }

    public record CreateTurmaRequest(
            String idMultiplicador,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            String tipo // e.g. "ONBOARDING"
    ) {}
}
