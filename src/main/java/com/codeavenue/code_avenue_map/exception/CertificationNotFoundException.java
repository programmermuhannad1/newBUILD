package com.codeavenue.code_avenue_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CertificationNotFoundException extends RuntimeException {
    public CertificationNotFoundException(Long id) {
        super("⚠️ Certification with ID " + id + " not found.");
    }
}
