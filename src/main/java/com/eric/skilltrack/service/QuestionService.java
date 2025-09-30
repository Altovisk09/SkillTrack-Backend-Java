package com.eric.skilltrack.service;

import com.eric.skilltrack.model.Question;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface QuestionService {

    Optional<Question> getById(String id) throws IOException;

    List<Question> getAll() throws IOException;

    Question createQuestion(String textoPergunta,
                            String alternativaA,
                            String alternativaB,
                            String alternativaC,
                            String alternativaD,
                            String respostaCorreta,
                            String categoria,
                            String foco,
                            String linkImagem) throws IOException;

    Question updateQuestion(String id, Question question) throws IOException;

    void deleteQuestion(String id) throws IOException;
}
