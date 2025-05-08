package com.codeavenue.code_avenue_map.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Roadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Roadmap name is required")
    private String name;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @Column(columnDefinition = "TEXT")
    private String roadmapStages;
}
