package com.example.managejob.repository;

import com.example.managejob.model.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleUser, Integer> {
    RoleUser findByRole(String role);
}
