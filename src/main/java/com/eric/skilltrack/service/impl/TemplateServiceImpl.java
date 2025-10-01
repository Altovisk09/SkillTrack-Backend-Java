package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.model.Template;
import com.eric.skilltrack.model.enums.TrainingType;
import com.eric.skilltrack.repository.QuestionRepository;
import com.eric.skilltrack.repository.TemplateRepository;
import com.eric.skilltrack.service.TemplateService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final QuestionRepository questionRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository, QuestionRepository questionRepository) {
        this.templateRepository = templateRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Template createTemplate(String nomeTreinamento, String categoria, String idsPerguntas,
                                   String setorFoco, String autor, TrainingType type) throws IOException {
        return templateRepository.createTemplate(nomeTreinamento, categoria, idsPerguntas, setorFoco, autor, type);
    }


    @Override
    public Template updateTemplate(Template template) throws IOException {
        return templateRepository.update(template);
    }

    @Override
    public void deleteTemplate(String templateId) throws IOException {
        templateRepository.deleteById(templateId);
    }

    @Override
    public List<Template> getAllTemplates() throws IOException {
        return templateRepository.findAll();
    }

    @Override
    public Template getTemplateById(String templateId) throws IOException {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template não encontrado: " + templateId));
    }

    @Override
    public List<Question> getQuestionsForTemplate(String templateId) throws IOException {
        Template template = getTemplateById(templateId);
        String csvIds = template.getIdsPerguntas();
        return questionRepository.getQuestionsFromCsvIds(csvIds);
    }
}
