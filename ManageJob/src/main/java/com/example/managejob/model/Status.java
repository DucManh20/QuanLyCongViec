package com.example.managejob.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Size(min = 1, max = 30)

    @Column(name = "status1", length = 30, unique = true)
    private String status1; // todoList, inProgress, Done, Review

    @OneToMany(mappedBy = "status",cascade =CascadeType.ALL)
    @JsonManagedReference
    private List<Task> task;
}
