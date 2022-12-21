package com.example.managejob.service.impl;

import com.example.managejob.model.Comment;
import com.example.managejob.repository.CommentRepository;
import com.example.managejob.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        super();
        this.commentRepository = commentRepository;
    }
    @Override
    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getCommentById(long id) {
        return null;
    }

    @Override
    public Page<Comment> getAllComment(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Long count() {
        return commentRepository.count();
    }
}
