package com.example.managejob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "estimate_hourse", length = 100)
    private String estimate_hourse;

    @Column(name = "spent_time", length = 100)
    private String spent_time;

    @Column(name = "start_date", length = 100)
    private String start_date;

    @Column(name = "end_date", length = 100)
    private String end_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;



//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "job_id")
//    private Job job;
}
