package com.example.managejob.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "groupU")
public class GroupUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name", length = 100, nullable = false)
    private String name;

    @Column(name= "description", length = 512)
        private String description;

    @Column(name="createdBy")
    private String createdBy;

    @CreatedDate
    @Column(name="createdAt")
    private Date createdAt;

    @OneToMany(mappedBy = "group",cascade =CascadeType.ALL)
    @JsonManagedReference
    private List<Task> task;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    private List<User> users;
}
