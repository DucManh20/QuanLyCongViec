package com.example.managejob.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "role_user")
public class RoleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @OneToMany(mappedBy = "roleUser")
    private Set<User> user;

}
