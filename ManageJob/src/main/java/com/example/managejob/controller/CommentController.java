package com.example.managejob.controller;

import com.example.managejob.dto.CommentDTO;
import com.example.managejob.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("comment")
public class CommentController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CommentService commentService;

    @GetMapping("/add")
    public String add() {
        return "admin/comment/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("comment") CommentDTO commentDTO) {
        commentService.add(commentDTO);
        return "redirect:/comment/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        commentService.delete(id);
        return "redirect:/comment/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        commentService.getList(page, model);
        return "admin/comment/list";
    }
}
