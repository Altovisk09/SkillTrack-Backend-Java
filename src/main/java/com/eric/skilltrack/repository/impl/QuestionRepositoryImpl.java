package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.repository.QuestionRepository;
import com.eric.skilltrack.repository.GenericRepository;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class QuestionRepositoryImpl extends GenericRepository<Question> implements QuestionRepository {

    private static final String SHEET_NAME = "Perguntas";

    private static final String COL_ID        = "ID_Pergunta";
    private static final String COL_TEXTO     = "Texto_Pergunta";
    private static final String COL_A         = "Alternativa_A";
    private static final String COL_B         = "Alternativa_B";
    private static final String COL_C         = "Alternativa_C";
    private static final String COL_D         = "Alternativa_D";
    private static final String COL_CORRETA   = "Resposta_Correta";
    private static final String COL_CATEGORIA = "Categoria";
    private static final String COL_FOCO      = "Setor_Foco";
    private static final String COL_IMAGEM    = "Link_Imagem";

    public QuestionRepositoryImpl(Sheets sheetsService, @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId) {
        super(sheetsService, spreadsheetId);
    }

    @Override
    protected String getSheetName() {
        return SHEET_NAME;
    }

    private String ns(String s) {
        return s == null ? "" : s;
    }

    @Override
    protected Question fromRow(List<Object> row) {
        List<String> c = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            c.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");
        }

        Question q = new Question();
        q.setIdPergunta(c.get(0));
        q.setTextoPergunta(c.get(1));
        q.setAlternativaA(c.get(2));
        q.setAlternativaB(c.get(3));
        q.setAlternativaC(c.get(4));
        q.setAlternativaD(c.get(5));
        q.setRespostaCorreta(c.get(6));
        q.setCategoria(c.get(7));
        q.setSetorFoco(c.get(8));
        q.setLinkImagem(c.get(9));
        return q;
    }

    @Override
    protected List<Object> toRow(Question e) {
        try {
            return buildRowFromMap(getSheetName(), toRowMap(e));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Map<String, Object> toRowMap(Question e) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(COL_ID,        ns(e.getIdPergunta()));
        map.put(COL_TEXTO,     ns(e.getTextoPergunta()));
        map.put(COL_A,         ns(e.getAlternativaA()));
        map.put(COL_B,         ns(e.getAlternativaB()));
        map.put(COL_C,         ns(e.getAlternativaC()));
        map.put(COL_D,         ns(e.getAlternativaD()));
        map.put(COL_CORRETA,   ns(e.getRespostaCorreta()));
        map.put(COL_CATEGORIA, ns(e.getCategoria()));
        map.put(COL_FOCO,      ns(e.getSetorFoco()));
        map.put(COL_IMAGEM,    ns(e.getLinkImagem()));
        return map;
    }

    /* ======= CRUD ======= */

    @Override
    public Optional<Question> findById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID, id);
        if (rowIndex == -1) return Optional.empty();
        return Optional.of(fromRow(getRowValues(rowIndex)));
    }

    @Override
    public List<Question> findAll() throws IOException {
        List<List<Object>> data = readAllData();
        List<Question> out = new ArrayList<>();
        for (List<Object> r : data) out.add(fromRow(r));
        return out;
    }

    @Override
    public Question save(Question entity) throws IOException {
        List<Object> row = toRow(entity);
        appendRowUserEntered(row, "J");
        return entity;
    }

    @Override
    public Question update(Question entity) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID, entity.getIdPergunta());
        if (rowIndex == -1) throw new IllegalArgumentException("Pergunta não encontrada: " + entity.getIdPergunta());
        updateRow(rowIndex, toRow(entity));
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID, id);
        if (rowIndex != -1) {
            updateCell(rowIndex, COL_CORRETA, "DELETADA"); // marca como deletada
        }
    }

    /* ======= Métodos específicos ======= */

    @Override
    public Question createQuestion(String textoPergunta,
                                   String alternativaA,
                                   String alternativaB,
                                   String alternativaC,
                                   String alternativaD,
                                   String respostaCorreta,
                                   String categoria,
                                   String foco,
                                   String linkImagem) throws IOException {

        String id = "QST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Question q = new Question();
        q.setIdPergunta(id);
        q.setTextoPergunta(textoPergunta);
        q.setAlternativaA(alternativaA);
        q.setAlternativaB(alternativaB);
        q.setAlternativaC(alternativaC);
        q.setAlternativaD(alternativaD);
        q.setRespostaCorreta(respostaCorreta);
        q.setCategoria(ns(categoria));
        q.setSetorFoco(ns(foco));
        q.setLinkImagem(ns(linkImagem));

        save(q);
        return q;
    }
}
