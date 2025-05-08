// UserUpdateDTO with new fields
package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateDTO {

    @NotBlank(message = "الاسم الأول لا يمكن أن يكون فارغًا")
    @Size(min = 2, max = 50, message = "يجب أن يكون الاسم الأول بين 2 و 50 حرفًا")
    private String firstName;

    @NotBlank(message = "الاسم الأخير لا يمكن أن يكون فارغًا")
    @Size(min = 2, max = 50, message = "يجب أن يكون الاسم الأخير بين 2 و 50 حرفًا")
    private String lastName;

    @Email(message = "يجب إدخال بريد إلكتروني صحيح")
    @NotBlank(message = "البريد الإلكتروني لا يمكن أن يكون فارغًا")
    private String email;

    @Size(max = 100, message = "يجب أن يكون اسم الدولة أقل من 100 حرف")
    private String country;

    @Size(max = 100, message = "يجب أن يكون اسم المدينة أقل من 100 حرف")
    private String city;

    @Size(min = 6, message = "يجب أن تتكون كلمة المرور من 6 أحرف على الأقل")
    private String password;

    @Size(min = 6, message = "يجب أن تتكون كلمة المرور من 6 أحرف على الأقل")
    private String confirmPassword;

    // new fields
    private User.Gender gender;
    private User.EducationLevel educationLevel;
    private User.UniversityCollege universityCollege;
    private LocalDate birthDate;
    private String phoneNumber;
}
