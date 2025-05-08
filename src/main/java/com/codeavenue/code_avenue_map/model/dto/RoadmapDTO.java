// RoadmapDTO.java
package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapDTO {
    private Long id;
    private String name;
    private String roadmapStages;
    private Long fieldId;
}
