package com.eric.skilltrack.service;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.model.Template;
import com.eric.skilltrack.model.enums.TrainingType;

import java.io.IOException;
import java.util.List;

public interface TemplateService {

    Template createTemplate(String nomeTreinamento,
                            String categoria,
                            String idsPerguntas,
                            String setorFoco,
                            String autor,
                            TrainingType type) throws IOException;

    Template updateTemplate(Template template) throws IOException;

    void deleteTemplate(String templateId) throws IOException;

    List<Template> getAllTemplates() throws IOException;

    Template getTemplateById(String templateId) throws IOException;

    List<Question> getQuestionsForTemplate(String templateId) throws IOException;
}
