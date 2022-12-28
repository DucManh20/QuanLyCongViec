package com.example.managejob.repository;

import com.example.managejob.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT b FROM Comment b WHERE b.task.id =:x")
    List<Comment> searchByTaskId(@Param("x") int id);

}
