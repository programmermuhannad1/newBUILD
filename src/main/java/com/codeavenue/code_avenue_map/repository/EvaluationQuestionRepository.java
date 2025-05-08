package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.EvaluationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationQuestionRepository extends JpaRepository<EvaluationQuestion, Long> {
    boolean existsByQuestionText(String questionText);

}
