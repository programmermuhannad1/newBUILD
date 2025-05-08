package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<Exam,Long> {
    @Query("SELECT COUNT(e) > 0 FROM Exam e")
    boolean existsAnyExam();
}
