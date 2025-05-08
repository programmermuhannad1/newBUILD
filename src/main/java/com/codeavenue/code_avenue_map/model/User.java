package com.codeavenue.code_avenue_map.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String country;
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, INACTIVE
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum EducationLevel {
        GRADUATE, UNIVERSITY, SECONDARY, INTERMEDIATE
    }

    public enum UniversityCollege {
        ENGINEERING, COMPUTER_SCIENCE, SCIENCE, THEORETICAL
    }

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    private UniversityCollege universityCollege;

    private LocalDate birthDate;

    private String phoneNumber;

    public User(String firstName, String lastName, String email, String password, String country, String city, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.role = role;
        this.status = Status.ACTIVE;
    }

    public void setActive(boolean isActive) {
        this.status = isActive ? Status.ACTIVE : Status.INACTIVE;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (role) {
            case SUPER_ADMIN -> List.of(
                    () -> "ROLE_SUPER_ADMIN",
                    () -> "ROLE_ADMIN",
                    () -> "ROLE_USER"
            );
            case ADMIN -> List.of(
                    () -> "ROLE_ADMIN",
                    () -> "ROLE_USER"
            );
            case USER -> List.of(() -> "ROLE_USER");
        };
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == Status.ACTIVE;
    }
}
