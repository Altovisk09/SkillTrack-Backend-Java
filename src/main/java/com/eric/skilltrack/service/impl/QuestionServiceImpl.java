package com.eric.skilltrack.service.impl;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.repository.QuestionRepository;
import com.eric.skilltrack.service.QuestionService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository repository;

    public QuestionServiceImpl(QuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Question> getById(String id) throws IOException {
        return repository.findById(id);
    }

    @Override
    public List<Question> getAll() throws IOException {
        return repository.findAll();
    }

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

        return repository.createQuestion(textoPergunta, alternativaA, alternativaB, alternativaC,
                alternativaD, respostaCorreta, categoria, foco, linkImagem);
    }

    @Override
    public Question updateQuestion(String id, Question question) throws IOException {
        question.setIdPergunta(id);
        return repository.update(question);
    }

    @Override
    public void deleteQuestion(String id) throws IOException {
        repository.deleteById(id);
    }
}
