package com.example.managejob.repository;

import com.example.managejob.model.Status;
import com.example.managejob.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    @Query("SELECT u FROM Status u WHERE u.status1 LIKE %:x% ")
    Page<Status> searchByStatus(@Param("x") String status, Pageable pageable);

    Status findByStatus1(String status);
}
