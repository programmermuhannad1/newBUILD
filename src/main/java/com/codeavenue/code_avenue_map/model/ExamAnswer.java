package com.codeavenue.code_avenue_map.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExamAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String answerText;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private ExamQuestion question;

    @Column(nullable = false)
    private int score;
}