package com.example.managejob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "password", length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "role", length = 30)
    private String role;

    @Column(name = "avatar", length = 150)
    private String avatar;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "modifi_by", length = 150)
    private String modifyBy;

    @Transient
    private MultipartFile file;

    @OneToMany(mappedBy = "user")
    private Set<Task> tasks;



}
