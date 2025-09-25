package com.eric.skilltrack.controller;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.service.OnboardingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turmas")
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    // POST /api/turmas  -> cria turma
    @PostMapping
    public ResponseEntity<Onboarding> criar(@Valid @RequestBody CriarTurmaRequest req) throws IOException {
        Onboarding o = service.createTurma(
                req.idMultiplicador(),
                req.idMultiplicadorReserva(),
                req.dataInicio()
        );
        // 201 + Location header
        return ResponseEntity
                .created(URI.create("/api/turmas/" + o.getIdTurma()))
                .body(o);
    }

    // PATCH /api/turmas/{idTurma}/reserva  -> define/atualiza reserva
    @PatchMapping("/{idTurma}/reserva")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void definirReserva(@PathVariable String idTurma,
                               @Valid @RequestBody DefinirReservaRequest req) throws IOException {
        service.setMultiplicadorReserva(idTurma, req.idMultiplicadorReserva());
    }

    // GET /api/turmas/{idTurma}
    @GetMapping("/{idTurma}")
    public ResponseEntity<Onboarding> buscar(@PathVariable String idTurma) throws IOException {
        return service.findById(idTurma)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/turmas
    @GetMapping
    public List<Onboarding> listar() throws IOException {
        return service.findAll();
    }

    // PUT /api/turmas/{idTurma} -> atualiza linha inteira (opcional)
    @PutMapping("/{idTurma}")
    public ResponseEntity<Onboarding> atualizar(@PathVariable String idTurma,
                                                @Valid @RequestBody AtualizarTurmaRequest req) throws IOException {
        Onboarding existente = service.findById(idTurma)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada: " + idTurma));

        // aplica campos (os que não forem nulos)
        if (req.idMultiplicador() != null) existente.setIdMultiplicador(req.idMultiplicador());
        if (req.idMultiplicadorReserva() != null) existente.setIdMultiplicadorReserva(req.idMultiplicadorReserva());
        if (req.dataInicio() != null) existente.setDataInicio(req.dataInicio().toString()); // repo formata pra dd/MM/yyyy
        if (req.status() != null) existente.setStatus(req.status());

        Onboarding atualizado = service.update(existente);
        return ResponseEntity.ok(atualizado);
    }

    // DELETE /api/turmas/{idTurma}
    @DeleteMapping("/{idTurma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String idTurma) throws IOException {
        service.deleteById(idTurma);
    }

    /* ==== DTOs ==== */

    public record CriarTurmaRequest(
            @NotBlank String idMultiplicador,
            String idMultiplicadorReserva,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio
    ) {}

    public record DefinirReservaRequest(
            @NotBlank String idMultiplicadorReserva
    ) {}

    public record AtualizarTurmaRequest(
            String idMultiplicador,
            String idMultiplicadorReserva,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            String status // se quiser permitir override manual
    ) {}

    /* ==== Tratamento simples de erros (opcional, local ao controller) ==== */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIO(IOException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .body("Falha de I/O com Google Sheets: " + ex.getMessage());
    }
}
