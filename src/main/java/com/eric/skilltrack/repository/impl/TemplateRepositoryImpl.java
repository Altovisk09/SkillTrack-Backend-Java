package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.model.Template;
import com.eric.skilltrack.model.enums.TrainingType;
import com.eric.skilltrack.repository.GenericRepository;
import com.eric.skilltrack.repository.QuestionRepository;
import com.eric.skilltrack.repository.TemplateRepository;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class TemplateRepositoryImpl extends GenericRepository<Template> implements TemplateRepository {

    private static final String SHEET_NAME = "Templates";

    private static final String COL_ID_TEMP  = "ID_Template";
    private static final String COL_NOME_TRE = "Nome_Treinamento";
    private static final String COL_CATE     = "Categoria";
    private static final String COL_IDS_PERG = "IDS_Perguntas";
    private static final String COL_TEMP     = "Setor_Foco";
    private static final String COL_AUTO     = "Autor";
    private static final String COL_TIPO     = "Tipo";

    private final QuestionRepository questionRepository;

    public TemplateRepositoryImpl(Sheets sheetsService,
                                  @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId,
                                  QuestionRepository questionRepository){
        super(sheetsService, spreadsheetId);
        this.questionRepository = questionRepository;
    }

    /* =================== Conversão de linhas =================== */

    @Override
    protected Template fromRow(List<Object> row) {
        List<String> c = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            c.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");
        }

        Template t = new Template();
        t.setIdTemplate(c.get(0));
        t.setNomeTreinamento(c.get(1));
        t.setCategoria(c.get(2));
        t.setIdsPerguntas(c.get(3)); // string CSV
        t.setSetorFoco(c.get(4));
        t.setAutor(c.get(5));
        t.setTipo(c.get(6).isBlank() ? TrainingType.ONBOARDING : TrainingType.valueOf(c.get(6)));
        return t;
    }

    @Override
    protected String getSheetName() {
        return SHEET_NAME;
    }

    @Override
    protected List<Object> toRow(Template entity) {
        try {
            return buildRowFromMap(getSheetName(), toRowMap(entity));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Map<String, Object> toRowMap(Template entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(COL_ID_TEMP,  entity.getIdTemplate());
        map.put(COL_NOME_TRE, entity.getNomeTreinamento());
        map.put(COL_CATE,     entity.getCategoria());
        map.put(COL_IDS_PERG, entity.getIdsPerguntas()); // já é CSV
        map.put(COL_TEMP,     entity.getSetorFoco());
        map.put(COL_AUTO,     entity.getAutor());
        map.put(COL_TIPO,     entity.getTipo() != null ? entity.getTipo().name() : TrainingType.ONBOARDING.name());
        return map;
    }

    /* =================== CRUD =================== */

    @Override
    public Optional<Template> findById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_TEMP, id);
        if (rowIndex == -1) return Optional.empty();
        return Optional.of(fromRow(getRowValues(rowIndex)));
    }

    @Override
    public List<Template> findAll() throws IOException {
        List<List<Object>> data = readAllData();
        List<Template> out = new ArrayList<>();
        for (List<Object> r : data) out.add(fromRow(r));
        return out;
    }

    @Override
    public Template save(Template entity) throws IOException {
        List<Object> row = toRow(entity);
        appendRowUserEntered(row, "J");
        return entity;
    }

    @Override
    public Template update(Template entity) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_TEMP, entity.getIdTemplate());
        if (rowIndex == -1) throw new IllegalArgumentException("Template não encontrado: " + entity.getIdTemplate());
        updateRow(rowIndex, toRow(entity));
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_ID_TEMP, id);
        if (rowIndex != -1) {
            deleteRow(rowIndex);
        }
    }

    /* =================== Métodos específicos =================== */

    @Override
    public List<Question> getQuestionsByIds(String csvIds) throws IOException {
        if (csvIds == null || csvIds.isBlank()) {
            return Collections.emptyList();
        }
        return questionRepository.getQuestionsFromCsvIds(csvIds);
    }

    @Override
    public Template createTemplate(String nomeTreinamento,
                                   String categoria,
                                   String idsPerguntas,
                                   String setorFoco,
                                   String autor,
                                   TrainingType type) throws IOException {

        Template t = new Template();
        t.setIdTemplate("TMP-" + UUID.randomUUID().toString().substring(0,6).toUpperCase());
        t.setNomeTreinamento(nomeTreinamento);
        t.setCategoria(categoria);
        t.setIdsPerguntas(idsPerguntas);
        t.setSetorFoco(setorFoco);
        t.setAutor(autor);
        t.setTipo(type != null ? type : TrainingType.ONBOARDING);

        return save(t);
    }
}
