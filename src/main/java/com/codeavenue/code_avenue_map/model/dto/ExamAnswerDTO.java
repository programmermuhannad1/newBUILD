package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswerDTO {
    private Long id;
    private String answerText;
    private int score;
}