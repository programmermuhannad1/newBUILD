package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.FeedbackChoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private Long id;
    private Long userId;
    private Long questionId;
    private FeedbackChoice feedbackChoice;
    }