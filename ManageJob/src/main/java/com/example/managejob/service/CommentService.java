package com.example.managejob.service;

import com.example.managejob.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComment();

    Comment createComment(Comment comment );

    void deleteComment(int id);

    Comment getCommentById(long id);

    Page<Comment> getAllComment(Pageable pageable);

    Long count();
}
