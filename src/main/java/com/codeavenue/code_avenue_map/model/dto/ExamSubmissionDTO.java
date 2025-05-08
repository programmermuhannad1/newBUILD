package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.FeedbackChoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmissionDTO {
    private List<FeedbackChoice> userAnswers;
    private Long userId;
    private Long examId;
}
