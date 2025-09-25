package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.Onboarding;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.*;

/**
 * Classe abstrata que implementa a interface BaseRepository,
 * fornecendo helpers prontos para ler/escrever dados no Google Sheets.
 */
public abstract class GenericRepository<T> implements BaseRepository<T, String> {

    protected final Sheets sheetsService;
    protected final String spreadsheetId;

    // Cache: guarda headers (nome da coluna -> índice zero-based)
    private final Map<String, Map<String, Integer>> headerCache = new HashMap<>();

    protected GenericRepository(Sheets sheetsService, String spreadsheetId) {
        this.sheetsService = sheetsService;
        this.spreadsheetId = spreadsheetId;
    }

    /** Nome da aba no Sheets (ex.: "Usuarios"). */
    protected abstract String sheetName();

    /** Converte uma linha em um objeto T. */
    protected abstract T fromRow(List<Object> row);

    /** Converte um objeto T em lista de colunas. */
    protected abstract List<Object> toRow(T entity);

    // Helpers reutilizáveis

    /** Lê um intervalo qualquer e retorna a matriz de valores. */
    protected List<List<Object>> readRange(String range) throws IOException {
        ValueRange vr = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        return vr.getValues() == null ? Collections.emptyList() : vr.getValues();
    }

    /** Escreve dados em um intervalo (sobrescreve). */
    protected void writeRange(String range, List<List<Object>> values) throws IOException {
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    /** Adiciona uma linha ao final da aba. */
    protected void appendRow(List<Object> row) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName() + "!A:Z", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    /** Atualiza uma linha inteira (sobrescreve). */
    protected void updateRow(int sheetRowIndex, List<Object> row) throws IOException {
        String lastCol = columnToLetter(Math.max(1, row.size()));
        String range = String.format("%s!A%d:%s%d", sheetName(), sheetRowIndex, lastCol, sheetRowIndex);
        ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    /** Atualiza apenas uma célula (coluna + linha). */
    protected void updateCell(int sheetRowIndex, String columnName, Object value) throws IOException {
        Map<String, Integer> header = loadHeaderIfNeeded(sheetName());
        Integer colIdx = header.get(columnName);
        if (colIdx == null) throw new IllegalArgumentException("Coluna não encontrada: " + columnName);

        String colLetter = columnToLetter(colIdx + 1); // colIdx é zero-based
        String range = String.format("%s!%s%d", sheetName(), colLetter, sheetRowIndex);

        ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(value)));
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    /** Lê todos os dados da aba (a partir da linha 2). */
    protected List<List<Object>> readAllData() throws IOException {
        return readRange(sheetName() + "!A2:Z");
    }

    /** Busca uma linha específica e retorna os valores. */
    protected List<Object> getRowValues(int sheetRowIndex) throws IOException {
        String range = String.format("%s!A%d:Z%d", sheetName(), sheetRowIndex, sheetRowIndex);
        List<List<Object>> rows = readRange(range);
        return rows.isEmpty() ? Collections.emptyList() : rows.get(0);
    }

    /**
     * Busca o número da linha onde columnName == value.
     * Ex.: procurar LDAP na coluna "LDAP".
     */
    @Override
    public int findRowIndexByColumn(String targetSheetName, String columnName, String value) throws IOException {
        Map<String, Integer> header = loadHeaderIfNeeded(targetSheetName);
        Integer colIdx = header.get(columnName);
        if (colIdx == null) return -1;

        String colLetter = columnToLetter(colIdx + 1);
        String range = String.format("%s!%s2:%s", targetSheetName, colLetter, colLetter);

        ValueRange vr = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> vals = vr.getValues() == null ? Collections.emptyList() : vr.getValues();

        for (int i = 0; i < vals.size(); i++) {
            List<Object> r = vals.get(i);
            if (!r.isEmpty() && value.equals(String.valueOf(r.get(0)))) {
                return i + 2; // +2 porque a leitura começa em A2
            }
        }
        return -1;
    }

    /** Carrega e cacheia os headers da aba (linha 1). */
    protected Map<String, Integer> loadHeaderIfNeeded(String sheet) throws IOException {
        if (headerCache.containsKey(sheet)) return headerCache.get(sheet);

        ValueRange vr = sheetsService.spreadsheets().values().get(spreadsheetId, sheet + "!1:1").execute();
        List<Object> headerRow = (vr.getValues() == null || vr.getValues().isEmpty())
                ? Collections.emptyList()
                : vr.getValues().get(0);

        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            map.put(String.valueOf(headerRow.get(i)), i); // zero-based index
        }
        headerCache.put(sheet, map);
        return map;
    }

    /** Limpa o cache do header de uma aba. */
    public void clearHeaderCache(String sheet) {
        headerCache.remove(sheet);
    }

    /** Converte índice de coluna numérico em letra (1 -> A, 27 -> AA). */
    private static String columnToLetter(int col) {
        StringBuilder sb = new StringBuilder();
        while (col > 0) {
            int rem = (col - 1) % 26;
            sb.insert(0, (char) ('A' + rem));
            col = (col - 1) / 26;
        }
        return sb.toString();
    }

    // Implementações genéricas de CRUD

    @Override
    public List<T> findAll() throws IOException {
        List<List<Object>> rows = readAllData();
        List<T> out = new ArrayList<>();
        for (List<Object> r : rows) {
            out.add(fromRow(r));
        }
        return out;
    }

    protected abstract Map<String, Object> toRowMap(T entity);

    @Override
    public abstract Optional<T> findById(String id) throws IOException;

    @Override
    public abstract T save(T entity) throws IOException;

    @Override
    public abstract T update(T entity) throws IOException;

    @Override
    public abstract void deleteById(String id) throws IOException;

    protected void appendRowUserEntered(List<Object> row, String lastCol) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName() + "!A:" + lastCol, body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    /**
     * Monta a linha (A..Z) respeitando a ordem dos headers da aba.
     * Para colunas que você não quer preencher, use null (fica vazio).
     */
    protected List<Object> buildRowFromMap(String sheet, Map<String, Object> rowMap) throws IOException {
        Map<String, Integer> header = loadHeaderIfNeeded(sheet);
        int size = header.size();
        List<Object> row = new ArrayList<>(Collections.nCopies(size, ""));
        for (Map.Entry<String, Object> e : rowMap.entrySet()) {
            Integer idx = header.get(e.getKey());
            if (idx != null) row.set(idx, e.getValue() == null ? "" : e.getValue());
        }
        return row;
    }

}
