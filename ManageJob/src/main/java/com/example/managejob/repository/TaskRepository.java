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
    @Query("SELECT u FROM Task u WHERE u.user.id =:x ")
    Page<Task> findListUserById(@Param("x") int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:idUser and u.group.id =:idGroup ")
    Page<Task> findAllTaskByUserGroup(@Param("idUser") int idUser, @Param("idGroup") int idGroup, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.user.id =:x ")
    List<Task> findListUserById(@Param("x") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x ")
    Page<Task> findListByStatus(@Param("x") String id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    List<Task> findListByStatus(@Param("x") String status, @Param("z") int id);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:x and u.user.id =:z")
    Page<Task> findListByStatusId(@Param("x") String status, @Param("z") int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.status.status1 =:status and u.user.id =:idUser and u.group.id =:idGroup ")
    Page<Task> findListByUserGroup(@Param("status") String status, @Param("idUser") int idUser, @Param("idGroup") int idGroup, Pageable pageable);
    Task findByName(String name);

    // filter
    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% AND u.user.id =:idCurrentUser AND u.startDate >= :start AND u.endDate <= :end AND u.group.name LIKe %:nameGroup%")
    Page<Task> searchByNameAndGroupAndDate(@Param("x") String name, @Param("idCurrentUser") int idCurrentUser, @Param("start") Date start, @Param("end") Date end, @Param("nameGroup") String nameGroup, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% and u.user.id =:idCurrentUser AND u.group.name LIKE %:nameGroup%")
    Page<Task> searchByNameAndGroup(@Param("x") String name, @Param("idCurrentUser") int idCurrentUser, @Param("nameGroup") String nameGroup, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% ")
    Page<Task> searchByName(@Param("x") String name, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.name LIKE %:x% and u.user.id =:id")
    Page<Task> searchByName(@Param("x") String name,int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.group.name LIKE %:x% and u.user.id =:id")
    Page<Task> searchByGroup(@Param("x") String name,int id, Pageable pageable);

    @Query("SELECT u FROM Task u WHERE u.startDate >= :start  and u.user.id =:id")
    Page<Task> searchByStartDate(@Param("start") Date date,int id, Pageable pageable);

    @Query("SELECT  u FROM Task u WHERE u.endDate <= :end  and u.user.id =:id")
    Page<Task> searchByEndDate(@Param("end") Date date,int id, Pageable pageable);

    @Query("SELECT  u FROM Task u WHERE  (:start is null or u.startDate >= :start)  and u.endDate <= :end and u.user.id =:id")
    Page<Task> searchByEndDateAndStartDate(@Param("start") Date startDated, @Param("end") Date endDate,int id, Pageable pageable);
}
