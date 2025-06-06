package com.codeavenue.code_avenue_map.model.dto;

import com.codeavenue.code_avenue_map.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateDTO {
    private Long userId;
    private Role newRole;
}
