package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.FeedbackChoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {
    private Long id;
    private String userName;
    private String questionText;
    private FeedbackChoice feedbackChoice;
}
