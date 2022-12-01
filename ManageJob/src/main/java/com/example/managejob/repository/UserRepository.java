package com.example.managejob.repository;

import com.example.managejob.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.name LIKE %:x% ")
    Page<User> searchByName(@Param("x") String name, Pageable pageable);
}
