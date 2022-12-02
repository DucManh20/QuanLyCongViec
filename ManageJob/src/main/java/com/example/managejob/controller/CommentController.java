package com.example.managejob.controller;

import com.example.managejob.model.Comment;
import com.example.managejob.model.Status;
import com.example.managejob.repository.CommentRepository;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.TaskRepository;
import com.example.managejob.repository.UserRepository;
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

    @Autowired
    CommentRepository cr;

    @Autowired
    TaskRepository tr;

    @Autowired
    UserRepository ur;

    @GetMapping("/add")
    public String add() {
        return "admin/comment/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("comment") Comment comment) {
        cr.save(comment);
        return "redirect:/comment/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        cr.deleteById(id);
        return "redirect:/comment/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Comment> pageUser = cr.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
//        model.addAttribute("listD", pageUser);
        long count = cr.count();
        model.addAttribute("count", count);
        return "admin/comment/list";
    }
}
