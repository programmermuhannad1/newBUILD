package com.codeavenue.code_avenue_map.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private Long id;
    private String name;
    private String platform;
    private String description;
    private double price;
    private Long fieldId;
    private String fieldName;
}
