package com.example.managejob.repository;

import com.example.managejob.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    Status findByStatus1(String status);

    @Query("SELECT u FROM Status u WHERE u.status1 LIKE %:x% ")
    List<Status> findByName(@Param("x") String name);


}
