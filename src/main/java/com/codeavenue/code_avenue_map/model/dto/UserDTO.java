// UserDTO with new fields
package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.Role;
import com.codeavenue.code_avenue_map.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String city;
    private Role role;

    // new fields
    private User.Gender gender;
    private User.EducationLevel educationLevel;
    private User.UniversityCollege universityCollege;
    private LocalDate birthDate;
    private String phoneNumber;
}
