package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion,Long> {
    List<ExamQuestion> findByExamId(Long examId);
}
