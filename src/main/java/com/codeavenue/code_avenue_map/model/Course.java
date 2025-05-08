package com.codeavenue.code_avenue_map.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    private String name;

    private String platform;

    @NotBlank(message = "Description is required")
    private String description;

    private double price;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;
}

