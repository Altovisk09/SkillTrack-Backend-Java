package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.model.Template;
import com.eric.skilltrack.model.enums.TrainingType;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends BaseRepository<Template, String> {
    @Override
    Optional<Template> findById(String s) throws IOException;

    @Override
    List<Template> findAll() throws IOException;

    @Override
    Template save(Template entity) throws IOException;

    @Override
    Template update(Template entity) throws IOException;

    @Override
    void deleteById(String s) throws IOException;

    @Override
    int findRowIndexByColumn(String sheetName, String columnName, String value) throws IOException;

    List<Question> getQuestionsByIds(String ids) throws IOException;

    Template createTemplate(String nomeTreinamento,
                        String categoria,
                        String idsPerguntas,
                        String setorFoco,
                        String autor,
                        TrainingType type) throws IOException;
}
