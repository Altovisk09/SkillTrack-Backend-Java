package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.repository.OnboardingRepository;
import com.eric.skilltrack.repository.GenericRepository;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class OnboardingRepositoryImpl extends GenericRepository<Onboarding> implements OnboardingRepository {

    private static final String SHEET_NAME = "Turmas";

    // Cabeçalhos
    private static final String COL_ID_TURMA   = "ID_Turma";                 // A
    private static final String COL_TURNO      = "Turno";                    // B (FÓRMULA)
    private static final String COL_ID_MULT    = "ID_Multiplicador";         // C
    private static final String COL_ID_MULTRES = "ID_Multiplicador_Reserva"; // D
    private static final String COL_DATA_INI   = "Data_Início";              // E (VALOR)
    private static final String COL_DATA_FIM   = "Data_Fim";                 // F (FÓRMULA)
    private static final String COL_STATUS     = "Status";                   // G (FÓRMULA)

    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public OnboardingRepositoryImpl(Sheets sheetsService, @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId) {
        super(sheetsService, spreadsheetId);
    }

    @Override protected String sheetName() { return SHEET_NAME; }

    @Override
    protected Onboarding fromRow(List<Object> row) {
        List<String> c = new ArrayList<>();
        for (int i = 0; i < 7; i++) c.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");
        Onboarding o = new Onboarding();
        o.setIdTurma(c.get(0));
        o.setTurno(c.get(1));
        o.setIdMultiplicador(c.get(2));
        o.setIdMultiplicadorReserva(c.get(3));
        o.setDataInicio(c.get(4));
        o.setDataFim(c.get(5));
        o.setStatus(c.get(6));
        return o;
    }

    @Override
    protected List<Object> toRow(Onboarding e) {
        Map<String, Object> map = toRowMap(e);
        try {
            return buildRowFromMap(sheetName(), map);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Map<String, Object> toRowMap(Onboarding e) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(COL_ID_TURMA,   e.getIdTurma());
        map.put(COL_TURNO,      e.getTurno());
        map.put(COL_ID_MULT,    e.getIdMultiplicador());
        map.put(COL_ID_MULTRES, e.getIdMultiplicadorReserva());
        map.put(COL_DATA_INI,   e.getDataInicio());
        map.put(COL_DATA_FIM,   e.getDataFim());
        map.put(COL_STATUS,     e.getStatus());
        return map;
    }


    /** Atualiza/define o multiplicador reserva (coluna D). */
    @Override
    public void setMultiplicadorReserva(String idTurma, String idMultiplicadorReserva) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, idTurma);
        if (rowIndex == -1) throw new IllegalArgumentException("Turma não encontrada: " + idTurma);
        updateCell(rowIndex, COL_ID_MULTRES, Optional.ofNullable(idMultiplicadorReserva).orElse(""));
    }

    /* ======= CRUD ======= */

    @Override
    public Optional<Onboarding> findById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, id);
        if (rowIndex == -1) return Optional.empty();
        return Optional.of(fromRow(getRowValues(rowIndex)));
    }

    @Override
    public List<Onboarding> findAll() throws IOException {
        List<List<Object>> data = readAllData();
        List<Onboarding> out = new ArrayList<>();
        for (List<Object> r : data) out.add(fromRow(r));
        return out;
    }

    @Override
    public Onboarding save(Onboarding entity) throws IOException {
        List<Object> row = toRow(entity);
        appendRowUserEntered(row, "G");
        return entity;
    }

    @Override
    public Onboarding update(Onboarding entity) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, entity.getIdTurma());
        if (rowIndex == -1) throw new IllegalArgumentException("Turma não encontrada: " + entity.getIdTurma());
        updateRow(rowIndex, toRow(entity));
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, id);
        if (rowIndex != -1) {
            updateCell(rowIndex, COL_STATUS, "Concluído"); // ou "Cancelada", se preferir
        }
    }

    /* ======= createTurma (public, casa com a interface) ======= */
    @Override
    public Onboarding createTurma(String IdMultiplicador, String IdMultiplicadorReserva, LocalDate dataFinal) throws IOException {
        // valida Data_Início ≥ hoje (dataFinal aqui é a data de INÍCIO)
        LocalDate hoje = LocalDate.now();
        if (dataFinal.isBefore(hoje)) dataFinal = hoje;

        String idTurma = generateTurmaId();
        String dataInicioBr = dataFinal.format(BR);

        // B: Turno buscado do MCG pelo ID do multiplicador (C)
        String fTurno =
                "=SEERRO(" +
                        "  ÍNDICE(Multiplicadores_Controle_Geral!$B:$B;" +
                        "         CORRESP(ÍNDICE($C:$C; LIN()); Multiplicadores_Controle_Geral!$A:$A; 0)" +
                        "  ); \"\"" +
                        ")";

        // F: Data_Fim = E + 5
        String fDataFim =
                "=SE(ÍNDICE($E:$E; LIN())=\"\"; \"\"; ÍNDICE($E:$E; LIN()) + 5)";

        // G: Status (até data fim → Em andamento; depois → Concluído)
        String fStatus =
                "=SE(" +
                        "  ÍNDICE($E:$E; LIN())=\"\";" +
                        "  \"\";" +
                        "  SE(ÍNDICE($F:$F; LIN()) < HOJE(); \"Concluído\"; \"Em andamento\")" +
                        ")";

        Map<String, Object> rowMap = new LinkedHashMap<>();
        rowMap.put(COL_ID_TURMA,   idTurma);
        rowMap.put(COL_TURNO,      fTurno); // fórmula
        rowMap.put(COL_ID_MULT,    IdMultiplicador);
        rowMap.put(COL_ID_MULTRES, Optional.ofNullable(IdMultiplicadorReserva).orElse(""));
        rowMap.put(COL_DATA_INI,   dataInicioBr);
        rowMap.put(COL_DATA_FIM,   fDataFim); // fórmula
        rowMap.put(COL_STATUS,     fStatus);  // fórmula

        List<Object> row = buildRowFromMap(sheetName(), rowMap);
        appendRowUserEntered(row, "G");

        Onboarding o = new Onboarding();
        o.setIdTurma(idTurma);
        o.setIdMultiplicador(IdMultiplicador);
        o.setIdMultiplicadorReserva(Optional.ofNullable(IdMultiplicadorReserva).orElse(""));
        o.setTurno("");        // calculado pela planilha
        o.setDataInicio(dataInicioBr);
        o.setDataFim("");      // calculado
        o.setStatus("");       // calculado
        return o;
    }

    /* ======= helpers ======= */
    private String generateTurmaId() {
        String hex = UUID.randomUUID().toString().replace("-", "");
        return "TRM-" + hex.substring(0, 6).toUpperCase();
    }
}
