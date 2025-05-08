package com.codeavenue.code_avenue_map.exception;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException(Long id) {
        super("⚠️ Feedback with ID " + id + " not found.");
    }
}