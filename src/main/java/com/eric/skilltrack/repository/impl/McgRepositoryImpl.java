package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.GeneralControlMultipliers;
import com.eric.skilltrack.model.Onboarding;
import com.eric.skilltrack.model.User;
import com.eric.skilltrack.repository.McgRepository;
import com.eric.skilltrack.repository.GenericRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

/**
 * Repositório da planilha "Multiplicadores_Controle_Geral".
 * Insere dados básicos (A..D) e fórmulas (E..I) com USER_ENTERED.
 */
@Repository
public class McgRepositoryImpl extends GenericRepository<GeneralControlMultipliers> implements McgRepository {

    private static final String SHEET_NAME   = "Multiplicadores_Controle_Geral";

    // Cabeçalhos (nomes das colunas na primeira linha)
    private static final String COL_ID_MULT   = "ID_Multiplicador"; // A
    private static final String COL_TURNO     = "Turno";            // B
    private static final String COL_LDAP      = "LDAP";             // C
    private static final String COL_NOME      = "Nome";             // D
    private static final String COL_ATIV_ATUAL= "Atividade_Atual";  // E (FÓRMULA)
    private static final String COL_SESS_TURMA= "Sessão/Turma";     // F (FÓRMULA)
    private static final String COL_DATA_INI  = "Data_Início";      // G (FÓRMULA)
    private static final String COL_DATA_FIM  = "Data_Fim";         // H (FÓRMULA)
    private static final String COL_STATUS_AT = "Status_Atual";     // I (FÓRMULA)

    public McgRepositoryImpl(
            Sheets sheetsService,
            @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId
    ) {
        super(sheetsService, spreadsheetId);
    }

    @Override
    protected String getSheetName() {
        return SHEET_NAME;
    }

    /* ==== Leitura/Conversão ==== */

    @Override
    protected GeneralControlMultipliers fromRow(List<Object> row) {
        List<String> c = new ArrayList<>();
        for (int i = 0; i < 9; i++) c.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");

        GeneralControlMultipliers g = new GeneralControlMultipliers();
        g.setIdMultiplicador(c.get(0));
        g.setTurno(c.get(1));
        g.setLdap(c.get(2));
        g.setNome(c.get(3));
        g.setAtividadeAtual(c.get(4));
        g.setSessaoTurma(c.get(5));
        g.setDataInicio(c.get(6));
        g.setDataFim(c.get(7));
        g.setStatusAtual(c.get(8));
        return g;
    }


    @Override
    protected List<Object> toRow(GeneralControlMultipliers e) {
        return Arrays.asList(
                e.getIdMultiplicador(),
                e.getTurno(),
                e.getLdap(),
                e.getNome(),
                e.getAtividadeAtual(),
                e.getSessaoTurma(),
                e.getDataInicio(),
                e.getDataFim(),
                e.getStatusAtual()
        );
    }

    /* ==== Regras MCG ==== */

    /** Verifica existência por LDAP (coluna C). */
    @Override
    public boolean existsByLdap(String ldap) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_LDAP, ldap);
        return rowIndex != -1;
    }

    /**
     * Insere A..D (ID curto, Turno, LDAP, Nome) e fórmulas E..I usando USER_ENTERED.
     */
    @Override
    public void insertBasicTrainerData(User user) throws IOException {
        // evita duplicação por LDAP
        if (existsByLdap(user.getLdap())) return;

        // ID curto (ex.: M-7b868fd2)
        String shortId = generateShortId();

        /*
         * Mapas:
         * Turmas:   A:ID_Turma | B:Turno | C:ID_Multiplicador | E:Data_Início | F:Data_Fim | G:Status
         * 4_Passos: A:ID_Passos| B:ID_Multiplicador | G:Data_Início | H:Data_Fim | I:Status
         */

        // E: Atividade_Atual
        String fAtividadeAtual =
                "=LET(" +
                        " id; ÍNDICE($A:$A; LIN());" +
                        " idn; REGEXREPLACE(id; \"\\s+\"; \"\");" +
                        " hasTurma; SOMARPRODUTO((REGEXREPLACE(Turmas!$C:$C; \"\\s+\"; \"\")=idn) * (Turmas!$G:$G=\"Em andamento\"));" +
                        " has4P; SOMARPRODUTO((REGEXREPLACE('4_Passos'!$B:$B; \"\\s+\"; \"\")=idn) * ('4_Passos'!$I:$I=\"Em andamento\"));" +
                        " SE(hasTurma>0; \"Integração\"; SE(has4P>0; \"4 Passos\"; \"Suporte Operacional\"))" +
                        ")";

        // F: Sessão/Turma
        String fSessaoTurma =
                "=SE(" +
                        " REGEXREPLACE(MINÚSCULA(ÍNDICE($E:$E;LIN()));\"\\s+\";\"\")=\"integração\";" +
                        " IFNA(" +
                        "   FILTER(" +
                        "     Turmas!A:A;" +
                        "     REGEXREPLACE(ARRUMAR(Turmas!C:C);\"\\s+\";\"\")=REGEXREPLACE(ARRUMAR(ÍNDICE($A:$A;LIN()));\"\\s+\";\"\");" +
                        "     MINÚSCULA(ARRUMAR(Turmas!G:G))=\"em andamento\"" +
                        "   );" +
                        "   \"Sem vínculo\"" +
                        " );" +
                        " SE(" +
                        "   REGEXREPLACE(MINÚSCULA(ÍNDICE($E:$E;LIN()));\"\\s+\";\"\")=\"4passos\";" +
                        "   IFNA(" +
                        "     FILTER(" +
                        "       '4_Passos'!A:A;" +
                        "       REGEXREPLACE(ARRUMAR('4_Passos'!B:B);\"\\s+\";\"\")=REGEXREPLACE(ARRUMAR(ÍNDICE($A:$A;LIN()));\"\\s+\";\"\");" +
                        "       MINÚSCULA(ARRUMAR('4_Passos'!I:I))=\"em andamento\"" +
                        "     );" +
                        "     \"Sem vínculo\"" +
                        "   );" +
                        "   \"Sem vínculo\"" +
                        " )" +
                        ")";

        // G: Data_Início
        String fDataInicio =
                "=SE(" +
                        " MINÚSCULA(ÍNDICE($E:$E;LIN()))=\"integração\";" +
                        " SEERRO(TEXTO(ÍNDICE(FILTER(Turmas!$E:$E;" +
                        "   REGEXREPLACE(Turmas!$A:$A;\"\\s+\";\"\")=REGEXREPLACE(ÍNDICE($F:$F;LIN());\"\\s+\";\"\");" +
                        "   Turmas!$G:$G=\"Em andamento\";" +
                        "   NÚM.CARACT(Turmas!$E:$E)>0" +
                        " );1);\"dd/mm/yyyy\");\"\");" +
                        " SE(MINÚSCULA(ÍNDICE($E:$E;LIN()))=\"4 passos\";" +
                        "   SEERRO(TEXTO(ÍNDICE(FILTER('4_Passos'!$G:$G;" +
                        "     REGEXREPLACE('4_Passos'!$A:$A;\"\\s+\";\"\")=REGEXREPLACE(ÍNDICE($F:$F;LIN());\"\\s+\";\"\");" +
                        "     '4_Passos'!I:I=\"Em andamento\";" +
                        "     NÚM.CARACT('4_Passos'!$G:$G)>0" +
                        "   );1);\"dd/mm/yyyy\");\"\");" +
                        "   \"\"" +
                        " )" +
                        ")";

        // H: Data_Fim
        String fDataFim =
                "=SE(" +
                        " MINÚSCULA(ÍNDICE($E:$E;LIN()))=\"integração\";" +
                        " SEERRO(TEXTO(ÍNDICE(FILTER(Turmas!$F:$F;" +
                        "   REGEXREPLACE(Turmas!$A:$A;\"\\s+\";\"\")=REGEXREPLACE(ÍNDICE($F:$F;LIN());\"\\s+\";\"\");" +
                        "   Turmas!$G:$G=\"Em andamento\";" +
                        "   NÚM.CARACT(Turmas!$F:$F)>0" +
                        " );1);\"dd/mm/yyyy\");\"\");" +
                        " SE(MINÚSCULA(ÍNDICE($E:$E;LIN()))=\"4 passos\";" +
                        "   SEERRO(TEXTO(ÍNDICE(FILTER('4_Passos'!$H:$H;" +
                        "     REGEXREPLACE('4_Passos'!$A:$A;\"\\s+\";\"\")=REGEXREPLACE(ÍNDICE($F:$F;LIN());\"\\s+\";\"\");" +
                        "     '4_Passos'!I:I=\"Em andamento\";" +
                        "     NÚM.CARACT('4_Passos'!$H:$H)>0" +
                        "   );1);\"dd/mm/yyyy\");\"\");" +
                        "   \"\"" +
                        " )" +
                        ")";

        // I: Status_Atual
        String fStatusAtual =
                "=LET(" +
                        " act; ÍNDICE($E:$E; LIN());" +
                        " SE(OU(act=\"Integração\"; act=\"4 Passos\"); \"Indisponível\"; \"Disponível\")" +
                        ")";

        List<Object> row = Arrays.asList(
                shortId,          // A: ID_Multiplicador (curto)
                user.getTurno(),  // B: Turno
                user.getLdap(),   // C: LDAP
                user.getNome(),   // D: Nome
                fAtividadeAtual,  // E
                fSessaoTurma,     // F
                fDataInicio,      // G
                fDataFim,         // H
                fStatusAtual      // I
        );

        appendRowUserEntered(row);
    }

    /** Gera um ID curto, estável o suficiente e legível. Ex.: M-7f3a2c9b */
    private String generateShortId() {
        String hex = UUID.randomUUID().toString().replace("-", "");
        return "M-" + hex.substring(0, 8);
    }

    /** Append com USER_ENTERED para avaliar fórmulas. */
    private void appendRowUserEntered(List<Object> row) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, SHEET_NAME + "!A:I", body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }

    /* ==== CRUD básico ==== */

    @Override
    public Optional<GeneralControlMultipliers> findById(String idMultiplicador) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_MULT, idMultiplicador);
        if (rowIndex == -1) return Optional.empty();
        return Optional.of(fromRow(getRowValues(rowIndex)));
    }

    @Override
    public List<GeneralControlMultipliers> findAll() throws IOException {
        List<List<Object>> rows = readAllData();
        List<GeneralControlMultipliers> out = new ArrayList<>();
        for (List<Object> r : rows) out.add(fromRow(r));
        return out;
    }

    @Override
    protected Map<String, Object> toRowMap(GeneralControlMultipliers entity) {
        return Map.of();
    }

    @Override
    public GeneralControlMultipliers save(GeneralControlMultipliers entity) throws IOException {
        appendRowUserEntered(toRow(entity));
        return entity;
    }

    @Override
    public GeneralControlMultipliers update(GeneralControlMultipliers entity) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_MULT, entity.getIdMultiplicador());
        if (rowIndex == -1) throw new IllegalArgumentException("Multiplicador não encontrado: " + entity.getIdMultiplicador());
        updateRow(rowIndex, toRow(entity));
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_MULT, id);
        if (rowIndex != -1) {
            updateCell(rowIndex, COL_STATUS_AT, "Removido");
        }
    }
}
