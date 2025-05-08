package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDTO {
    private Long id;
    private String questionText;
    private Long exam;
    private Long firstMajorId;
    private Long secondMajorId;
    private List<ExamAnswerDTO> answers;
}