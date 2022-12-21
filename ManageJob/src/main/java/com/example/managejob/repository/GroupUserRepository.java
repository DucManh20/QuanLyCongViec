package com.example.managejob.repository;

import com.example.managejob.model.GroupUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupUserRepository extends JpaRepository<GroupUser, Integer> {
    GroupUser findByName(String name);

    @Query("SELECT  u FROM GroupUser u WHERE  (:x is null or u.name LIKE %:x%)  and (:z is null or u.description LIKE %:z% )")
    Page<GroupUser> search(@Param("x") String name, @Param("z") String description, Pageable pageable);
}
