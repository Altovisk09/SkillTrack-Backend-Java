// OnboardingRepositoryImpl.java
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
        // Garante 7 colunas
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
        map.put(COL_TURNO,      e.getTurno()); // pode ser fórmula (Strings de fórmula tbm são USER_ENTERED)
        map.put(COL_ID_MULT,    e.getIdMultiplicador());
        map.put(COL_ID_MULTRES, e.getIdMultiplicadorReserva());
        map.put(COL_DATA_INI,   e.getDataInicio());
        map.put(COL_DATA_FIM,   e.getDataFim());   // pode ser fórmula
        map.put(COL_STATUS,     e.getStatus());    // pode ser fórmula
        return map;
    }

    /* ======= API de criação de turma ======= */

    /** Cria uma turma e atrela ao multiplicador. Reserva é opcional (pode ser null/blank). */
    public Onboarding createTurma(String idMultiplicador, String idMultiplicadorReserva, LocalDate dataInicio)
            throws IOException {

        // 1) valida Data_Início ≥ hoje
        LocalDate hoje = LocalDate.now();
        if (dataInicio.isBefore(hoje)) dataInicio = hoje;

        // 2) gera ID_Turma
        String idTurma = generateTurmaId();

        // 3) fórmulas dependentes da LIN() (independem de qual linha a planilha vai usar)
        String fTurno =
                "=SEERRO(" +
                        "  ÍNDICE(Multiplicadores_Controle_Geral!$B:$B;" +
                        "         CORRESP(ÍNDICE($C:$C; LIN()); Multiplicadores_Controle_Geral!$A:$A; 0)" +
                        "  ); \"\"" +
                        ")";

        String fDataFim =
                "=SE(ÍNDICE($E:$E; LIN())=\"\"; \"\"; ÍNDICE($E:$E; LIN()) + 5)";

        String fStatus =
                "=SE(" +
                        "  ÍNDICE($E:$E; LIN())=\"\";" +
                        "  \"\";" +
                        "  SE(ÍNDICE($F:$F; LIN()) < HOJE(); \"Concluído\"; \"Em andamento\")" +
                        ")";
        String dataInicioBr = dataInicio.format(BR);

        Map<String, Object> rowMap = new LinkedHashMap<>();
        rowMap.put(COL_ID_TURMA,   idTurma);                 // valor
        rowMap.put(COL_TURNO,      fTurno);                  // FÓRMULA
        rowMap.put(COL_ID_MULT,    idMultiplicador);         // valor
        rowMap.put(COL_ID_MULTRES, Optional.ofNullable(idMultiplicadorReserva).orElse("")); // valor (pode ser "")
        rowMap.put(COL_DATA_INI,   dataInicioBr);            // valor (já validado >= hoje)
        rowMap.put(COL_DATA_FIM,   fDataFim);                // FÓRMULA
        rowMap.put(COL_STATUS,     fStatus);                 // FÓRMULA

        List<Object> row = buildRowFromMap(sheetName(), rowMap);
        // última coluna é G
        appendRowUserEntered(row, "G");

        // 5) retorno do objeto
        Onboarding o = new Onboarding();
        o.setIdTurma(idTurma);
        o.setIdMultiplicador(idMultiplicador);
        o.setIdMultiplicadorReserva(Optional.ofNullable(idMultiplicadorReserva).orElse(""));
        o.setTurno(""); // será calculado pela planilha
        o.setDataInicio(dataInicioBr);
        o.setDataFim(""); // será calculado
        o.setStatus("");  // será calculado
        return o;
    }

    /** Atualiza/define o multiplicador reserva (coluna D). */
    public void setMultiplicadorReserva(String idTurma, String idMultiplicadorReserva) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, idTurma);
        if (rowIndex == -1) throw new IllegalArgumentException("Turma não encontrada: " + idTurma);
        updateCell(rowIndex, COL_ID_MULTRES, Optional.ofNullable(idMultiplicadorReserva).orElse(""));
    }

    /* ======= CRUD (caso queira usar também) ======= */

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
            updateCell(rowIndex, COL_STATUS, "Concluído"); // ou "Cancelada", conforme sua regra
        }
    }

    /* ======= helpers ======= */

    private String generateTurmaId() {
        String hex = UUID.randomUUID().toString().replace("-", "");
        return "TRM-" + hex.substring(0, 6).toUpperCase();
    }
}