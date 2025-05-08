package com.codeavenue.code_avenue_map.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(" كلمة المرور المشفرة: " + encodedPassword);
    }
}
