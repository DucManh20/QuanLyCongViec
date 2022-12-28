package com.example.managejob.controller;

import com.example.managejob.model.User;
import com.example.managejob.service.RoleService;
import com.example.managejob.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
@Controller
@RequestMapping("user")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("roleList", roleService.findAll());
        return "admin/user/add";
    }

    @GetMapping("/account")
    public String account(Principal principal, Model model) {
        userService.account(principal, model);
        return "admin/user/account";
    }

    @GetMapping("/accountById")
    public String accountById(Model model, @RequestParam("id") int id) {
        userService.accountById(model, id);
        return "admin/user/account";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute User user, Principal principal, Model model) throws IOException {
        userService.addPost(user, principal, model);
        return "admin/user/add";
    }

    @RequestMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
        userService.download(filename, response);
    }

    @GetMapping("/exportExcel")
    public void exportData(HttpServletResponse response) throws IOException {
        userService.exportData(response);
    }

    @GetMapping("/edit")
    public String editGet(@RequestParam("id") int id, Model model) {
        userService.editGet(id, model);
        return "admin/user/edit";
    }

    @PostMapping("/edit")
    public String editPost(@ModelAttribute("user") User user, Principal principal, Model model)
            throws IllegalStateException, IOException {
        userService.editPost(user, principal, model);
        return "admin/user/add";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        userService.delete(id);
        return "redirect:/user/list";
    }

    @GetMapping("/list")
    public String getList(Model model, @RequestParam(value = "page", required = false) Integer page) {
        userService.getList(model, page);
        return "admin/user/list";
    }


    @GetMapping("/search")
    public String search(@RequestParam("searchName") String name, Model model,
                         @RequestParam(value = "page", required = false) Integer page) {
        userService.search(name, model, page);
        return "admin/user/list";
    }
}

