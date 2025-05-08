package com.codeavenue.code_avenue_map.repository;

import com.codeavenue.code_avenue_map.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUser_Id(Long userId);


}
