package com.eric.skilltrack.controller;

import com.eric.skilltrack.model.Question;
import com.eric.skilltrack.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() throws IOException {
        return ResponseEntity.ok(questionService.getAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String id) throws IOException {
        Optional<Question> question = questionService.getById(id);
        return question.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) throws IOException {
        Question created = questionService.createQuestion(
                question.getTextoPergunta(),
                question.getAlternativaA(),
                question.getAlternativaB(),
                question.getAlternativaC(),
                question.getAlternativaD(),
                question.getRespostaCorreta(),
                question.getCategoria(),
                question.getSetorFoco(),
                question.getLinkImagem()
        );
        return ResponseEntity.ok(created);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable String id, @RequestBody Question question) throws IOException {
        Question updated = questionService.updateQuestion(id, question);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) throws IOException {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
