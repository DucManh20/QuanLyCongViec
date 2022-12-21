package com.example.managejob.model;

import com.example.managejob.service.AuthenticationProvider;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthenticationProvider authenticationProvider;

    @Column(name = "avatar", length = 150)
    private String avatar;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "modifi_by", length = 150)
    private String modifyBy;

    @Transient
    private MultipartFile file;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "role_id")
    private RoleUser roleUser;

    @OneToMany(mappedBy = "user",cascade =CascadeType.ALL)
    @JsonManagedReference
    private Set<Task> tasks;

    @OneToMany(mappedBy = "user",cascade =CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "user",cascade =CascadeType.ALL)
    @JsonManagedReference
    private List<Document> documents ;

    @ManyToMany(mappedBy = "users")
    @JsonBackReference
    private List<GroupUser> groups;
}
