package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.EvaluationQuestionNotFoundException;
import com.codeavenue.code_avenue_map.model.EvaluationQuestion;
import com.codeavenue.code_avenue_map.model.dto.EvaluationQuestionDTO;
import com.codeavenue.code_avenue_map.model.dto.EvaluationQuestionRequestDTO;
import com.codeavenue.code_avenue_map.repository.EvaluationQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationQuestionService {
    private final EvaluationQuestionRepository evaluationQuestionRepository;

    public EvaluationQuestionService(EvaluationQuestionRepository evaluationQuestionRepository) {
        this.evaluationQuestionRepository = evaluationQuestionRepository;
    }

    @Transactional
    public EvaluationQuestionDTO createQuestion(EvaluationQuestionRequestDTO questionRequest) {
        EvaluationQuestion question = new EvaluationQuestion();
        question.setQuestionText(questionRequest.getQuestionText());

        EvaluationQuestion savedQuestion = evaluationQuestionRepository.save(question);
        return mapToDTO(savedQuestion);
    }

    public List<EvaluationQuestionDTO> getAllQuestions() {
        return evaluationQuestionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EvaluationQuestionDTO getQuestionById(Long id) {
        EvaluationQuestion question = evaluationQuestionRepository.findById(id)
                .orElseThrow(() -> new EvaluationQuestionNotFoundException("⚠️ Evaluation Question not found with ID: " + id));
        return mapToDTO(question);
    }

    @Transactional
    public EvaluationQuestionDTO updateQuestion(Long id, EvaluationQuestionRequestDTO updatedQuestion) {
        EvaluationQuestion question = evaluationQuestionRepository.findById(id)
                .orElseThrow(() -> new EvaluationQuestionNotFoundException("⚠️ Evaluation Question not found with ID: " + id));

        question.setQuestionText(updatedQuestion.getQuestionText());
        EvaluationQuestion updated = evaluationQuestionRepository.save(question);

        return mapToDTO(updated);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        EvaluationQuestion question = evaluationQuestionRepository.findById(id)
                .orElseThrow(() -> new EvaluationQuestionNotFoundException("⚠️ Evaluation Question not found with ID: " + id));

        evaluationQuestionRepository.delete(question);
    }

    private EvaluationQuestionDTO mapToDTO(EvaluationQuestion question) {
        return new EvaluationQuestionDTO(
                question.getId(),
                question.getQuestionText()
        );
    }
}
