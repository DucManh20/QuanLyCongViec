package com.example.managejob.dto;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserDTO {

    private int id;

    private String name;

    private String description;

    private String createdBy;

    private Date createdAt;

    private List<Task> task;

    private List<User> users;
}
