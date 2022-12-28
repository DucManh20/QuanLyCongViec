package com.example.managejob.dto;


import com.example.managejob.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserDTO {
    private int id;

    private String role;

    private Set<User> user;
}
