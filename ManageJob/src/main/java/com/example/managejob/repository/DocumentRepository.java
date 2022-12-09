package com.example.managejob.repository;

import com.example.managejob.model.Comment;
import com.example.managejob.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("SELECT b FROM Document b WHERE b.task.id =:x")
    List<Document> searchByTaskId(@Param("x") int id);
}
