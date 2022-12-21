package com.example.managejob.dto;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private int id;
    private String content;
    private Date createdAt;
    private Integer user_id;
    private Integer task_id;
}
