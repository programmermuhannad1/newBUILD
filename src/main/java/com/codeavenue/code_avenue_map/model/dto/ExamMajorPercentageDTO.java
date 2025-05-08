package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExamMajorPercentageDTO {
    private Long majorId;
    private String majorName;
    private double percentage;
}
