package com.example.managejob.repository;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Query("SELECT u FROM Task u WHERE u.user.id =:idUser and u.group.id =:idGroup ")
    Page<Task> findAllTaskByUserGroup(@Param("idUser") int idUser, @Param("idGroup") int idGroup, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:x ")
    List<Task> findListUserById(@Param("x") int id);

    @Query("SELECT u FROM Task u WHERE u.group.id =:x ")
    List<Task> findByGroup(@Param("x") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x ")
    Page<Task> findListByStatus(@Param("x") String id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:x and u.check = 1")
    List<Task> checkEndDate(@Param("x") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    List<Task> findListByStatus(@Param("x") String status, @Param("z") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    Page<Task> findListByStatusId(@Param("x") String status, @Param("z") int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:status and u.user.id =:idUser and u.group.id =:idGroup ")
    Page<Task> findListByUserGroup(@Param("status") String status, @Param("idUser") int idUser, @Param("idGroup") int idGroup, Pageable pageable);
    Task findByName(String name);

    // filter
    @Query("SELECT  u FROM Task u WHERE  (:start is null or u.startDate >= :start)  and (:end is null or u.endDate <= :end) and (:nameGroup is null or u.group.name LIKE %:nameGroup% ) and (:name is null or u.name LIKE %:name% )  and u.user.id =:id")
    Page<Task> search(@Param("start") Date startDated, @Param("end") Date endDate,@Param("nameGroup") String nameGroup,@Param("name") String name, int id, Pageable pageable);

    @Query("SELECT  u FROM Task u WHERE  u.endDate <= :dateNow and u.check = 0")
    List<Task> checkEndDate(@Param("dateNow") Date dateNow);

    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% ")
    List<Task> searchByName(@Param("x") String name);
}
