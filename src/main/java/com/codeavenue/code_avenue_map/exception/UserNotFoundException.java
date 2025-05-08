package com.codeavenue.code_avenue_map.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("❌ المستخدم ذو المعرف " + id + " غير موجود.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
