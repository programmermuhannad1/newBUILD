package com.codeavenue.code_avenue_map.model.dto;


import com.codeavenue.code_avenue_map.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    @Size(min = 6, message = "Confirm Password must be at least 6 characters long")
    private String confirmPassword;

    private String country;
    private String city;

    private User.Gender gender;
    private User.EducationLevel educationLevel;
    private User.UniversityCollege universityCollege;
    private LocalDate birthDate;
    private String phoneNumber;
}
