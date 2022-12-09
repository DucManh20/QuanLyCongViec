package com.example.managejob.repository;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% ")
    Page<Task> searchByName(@Param("x") String name, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% and u.user.id =:id")
    Page<Task> searchByName(@Param("x") String name,int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:x ")
    Page<Task> findListUserById(@Param("x") int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:x ")
    List<Task> findListUserById(@Param("x") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x ")
    Page<Task> findListByStatus(@Param("x") String id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    List<Task> findListByStatus(@Param("x") String status, @Param("z") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    Page<Task> findListByStatusId(@Param("x") String status, @Param("z") int id, Pageable pageable);
    Task findByName(String name);
//

}
