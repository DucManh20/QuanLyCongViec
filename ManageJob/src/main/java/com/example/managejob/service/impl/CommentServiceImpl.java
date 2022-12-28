package com.example.managejob.service.impl;

import com.example.managejob.dto.CommentDTO;
import com.example.managejob.model.Comment;
import com.example.managejob.repository.CommentRepository;
import com.example.managejob.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public Comment add(CommentDTO commentDTO) {
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        return commentRepository.save(comment);
    }

    @Override
    public void delete(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void getList(Integer page, Model model
    ){
        page = (page == null || page < 0) ? 0 : page;
        int size = 10;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Comment> pageUser = commentRepository.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = commentRepository.count();
        model.addAttribute("count", count);
    }

    @Override
    public Long count() {
        return commentRepository.count();
    }
}
