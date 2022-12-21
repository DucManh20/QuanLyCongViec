package com.example.managejob.controller;

import com.example.managejob.dto.CommentDTO;
import com.example.managejob.model.Comment;
import com.example.managejob.repository.CommentRepository;
import com.example.managejob.repository.TaskRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("comment")
public class CommentController {
    private final ModelMapper modelMapper;
    private final CommentService commentService;

    public CommentController(ModelMapper modelMapper, CommentService commentService) {
        this.modelMapper = modelMapper;
        this.commentService = commentService;
    }

    @GetMapping("/add")
    public String add() {
        return "admin/comment/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("comment") CommentDTO commentDTO) {
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        commentService.createComment(comment);
        return "redirect:/comment/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        commentService.deleteComment(id);
        return "redirect:/comment/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 10;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Comment> pageUser = commentService.getAllComment(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = commentService.count();
        model.addAttribute("count", count);
        return "admin/comment/list";
    }
}
