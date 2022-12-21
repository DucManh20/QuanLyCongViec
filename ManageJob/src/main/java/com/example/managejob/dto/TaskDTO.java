package com.example.managejob.dto;

import com.example.managejob.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private int id;

    private String name;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private String modifyBy;

    private User user;

    private Status status;

    private GroupUser group;

    private List<Comment> comments;

    private List<Document> documents;

}
