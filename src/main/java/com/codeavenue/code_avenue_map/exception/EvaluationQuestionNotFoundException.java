package com.codeavenue.code_avenue_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EvaluationQuestionNotFoundException extends RuntimeException {
    public EvaluationQuestionNotFoundException(String message) {
        super(message);
    }
}
