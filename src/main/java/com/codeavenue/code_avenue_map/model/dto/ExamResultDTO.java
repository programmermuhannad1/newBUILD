package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    private Long id;
    private double score;
    private Long userId;
    private Long examId;
    private Long majorId;
    @NotBlank(message = "Major name is required")
    private String suggestedMajorName;
    private List<ExamMajorPercentageDTO> majorPercentages;
}
