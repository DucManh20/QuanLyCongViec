package com.example.managejob.dto;

import com.example.managejob.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;
import java.util.List;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;

    private String name;

    private String password;

    private String email;

    private String avatar;

    private RoleUser roleUser;

    private Set<Task> tasks;

    private List<Comment> comments;

    private List<Document> documents;

    private List<GroupUser> groups;

    @Transient
    private MultipartFile file;
}
