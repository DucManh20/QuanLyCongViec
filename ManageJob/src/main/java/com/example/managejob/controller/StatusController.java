package com.example.managejob.controller;

import com.example.managejob.model.Status;
import com.example.managejob.model.User;
import com.example.managejob.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("status")
public class StatusController {

    @Autowired
    StatusRepository sr;

    @GetMapping("/add")
    public String add(){
        return "admin/status/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("status") Status status, Model model){
        int k = 0;
        Pattern statusP = Pattern.compile("^^[0-9a-zA-Z]{1,}$");
        Matcher m2 = statusP.matcher(status.getStatus1());

        Status statusCheck = sr.findByStatus1(status.getStatus1());

        if (statusCheck != null) {
            model.addAttribute("errStatus", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errStatus", "Not empty");
            k = 1;
        }
        if(k == 1){
            model.addAttribute("status1", status.getStatus1());
            return "admin/status/add";
        }
        sr.save(status);
        return "redirect:/status/list";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model, HttpSession session){
        Status s = sr.findById(id).orElse(null);
        model.addAttribute("status", s);
        session.setAttribute("idStatus", id);
        return "admin/status/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Status status, Model model, HttpSession session){
        status.setId((int)session.getAttribute("idStatus"));
        int k = 0;
        Pattern statusP = Pattern.compile("^^[0-9a-zA-Z]{1,}$");
        Matcher m2 = statusP.matcher(status.getStatus1());

        Status statusCheck = sr.findByStatus1(status.getStatus1());

        if (statusCheck != null && statusCheck.getId() != (int)session.getAttribute("idStatus")) {
            model.addAttribute("errStatus", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errStatus", "Not empty");
            k = 1;
        }
        if(k == 1){
            model.addAttribute("status1", status.getStatus1());
            return "admin/status/edit";
        }
        sr.save(status);
        return "redirect:/status/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        sr.deleteById(id);
        return "redirect:/status/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Status> pageUser = sr.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
//        model.addAttribute("listD", pageUser);
        long count = sr.count();
        model.addAttribute("count", count);
        return "admin/status/list";
    }
}
