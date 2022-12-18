package com.example.managejob.repository;

import com.example.managejob.model.GroupUser;
import com.example.managejob.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupUserRepository extends JpaRepository<GroupUser, Integer> {

//    @Query("SELECT u.users FROM GroupUser u WHERE u.  ")
//    Page<User> searchByName(@Param("x") String name, Pageable pageable);

    GroupUser findByName(String name);
}
