package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.model.enums.CategoryQuestion;
import com.eric.skilltrack.model.enums.FocusArea;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends BaseRepository<Question, String> {

    @Override
    Optional<Question> findById(String id) throws IOException;

    @Override
    List<Question> findAll() throws IOException;

    @Override
    Question save(Question entity) throws IOException;

    @Override
    Question update(Question entity) throws IOException;

    @Override
    void deleteById(String id) throws IOException;

    @Override
    int findRowIndexByColumn(String sheetName, String columnName, String value) throws IOException;

    Question createQuestion(String textoPergunta,
                            String alternativaA,
                            String alternativaB,
                            String alternativaC,
                            String alternativaD,
                            String respostaCorreta,
                            String categoria,
                            String foco,
                            String linkImagem) throws IOException;
}
