package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.repository.GenericRepository;
import com.eric.skilltrack.repository.OnboardingRepository;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class OnboardingRepositoryImpl extends GenericRepository<Onboarding> implements OnboardingRepository {

    private static final String SHEET_NAME = "Turmas";

    // Mapeamento das colunas
    private static final String COL_ID_TURMA = "ID_Turma";
    private static final String COL_TURNO = "Turno";
    private static final String COL_ID_MULT = "ID_Multiplicador";
    private static final String COL_ID_MULT_RES = "ID_Multiplicador_Reserva";
    private static final String COL_DATA_INI = "Data_Início";
    private static final String COL_DATA_FIM = "Data_Fim";
    private static final String COL_STATUS = "Status";

    public OnboardingRepositoryImpl(Sheets sheetsService, @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId) {
        super(sheetsService, spreadsheetId);
    }

    @Override
    protected String sheetName() { return SHEET_NAME; }

    @Override
    protected Onboarding fromRow(List<Object> row) {
        // ATENÇÃO: Verifique se esta ordem bate com a sua planilha "Turmas"
        List<String> cols = new ArrayList<>();
        for (int i = 0; i < 7; i++) { // 7 colunas no total
            cols.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");
        }

        Onboarding o = new Onboarding();
        o.setIdTurma(cols.get(0));
        o.setTurno(cols.get(1));
        o.setIdMultiplicador(cols.get(2));
        o.setIdMultiplicadorReserva(cols.get(3));
        o.setDataInicio(cols.get(4));
        o.setDataFim(cols.get(5));
        o.setStatus(cols.get(6));
        return o;
    }

    @Override
    protected List<Object> toRow(Onboarding entity) {
        return List.of();
    }

    @Override
    protected Map<String, Object> toRowMap(Onboarding entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(COL_ID_TURMA, entity.getIdTurma());
        map.put(COL_TURNO, entity.getTurno());
        map.put(COL_ID_MULT, entity.getIdMultiplicador());
        map.put(COL_ID_MULT_RES, entity.getIdMultiplicadorReserva());
        map.put(COL_DATA_INI, entity.getDataInicio());
        map.put(COL_DATA_FIM, entity.getDataFim());
        map.put(COL_STATUS, entity.getStatus());
        return map;
    }

    @Override
    public Optional<Onboarding> findById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, id);
        if (rowIndex == -1) return Optional.empty();
        return Optional.of(fromRow(getRowValues(rowIndex)));
    }

    @Override
    public List<Onboarding> findAll() throws IOException {
        List<List<Object>> data = readAllData();
        List<Onboarding> results = new ArrayList<>();
        for (List<Object> row : data) {
            results.add(fromRow(row));
        }
        return results;
    }

    @Override
    public Onboarding save(Onboarding entity) throws IOException {
        List<Object> row = buildRowFromMap(sheetName(), toRowMap(entity));
        appendRow(row);
        return entity;
    }

    private List<Object> buildRowFromMap(String s, Map<String, Object> rowMap) {
        return List.of();
    }

    @Override
    public Onboarding update(Onboarding entity) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, entity.getIdTurma());
        if (rowIndex == -1) throw new IllegalArgumentException("Turma não encontrada: " + entity.getIdTurma());
        List<Object> row = buildRowFromMap(sheetName(), toRowMap(entity));
        updateRow(rowIndex, row);
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), COL_ID_TURMA, id);
        if (rowIndex != -1) {
            // A API do Sheets não tem um "delete row" simples.
            // A melhor abordagem é limpar a linha ou atualizar o status.
            updateCell(rowIndex, COL_STATUS, "CANCELADA");
        }
    }
}