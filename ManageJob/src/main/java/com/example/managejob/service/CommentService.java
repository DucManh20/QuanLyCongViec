package com.example.managejob.service;

import com.example.managejob.dto.CommentDTO;
import com.example.managejob.model.Comment;
import org.springframework.ui.Model;

public interface CommentService {
    Comment add(CommentDTO commentDTO);

    void delete(int id);

    void getList(Integer page, Model model);

    Long count();
}
