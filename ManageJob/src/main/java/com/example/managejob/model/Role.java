package com.example.managejob.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "role", nullable = false, unique = true)
    private String role;
}
