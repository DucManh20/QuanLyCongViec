package com.example.managejob.repository;

import com.example.managejob.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% ")
    Page<Task> searchByName(@Param("x") String name, Pageable pageable);

}
