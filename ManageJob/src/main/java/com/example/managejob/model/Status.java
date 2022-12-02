package com.example.managejob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "status1", length = 30, unique = true)
    private String status1; // todoList, inProgress, Done, Review

//    @OneToOne(mappedBy = "status")
//    @OneToOne(mappedBy = "status")
//    private Task task;

}
