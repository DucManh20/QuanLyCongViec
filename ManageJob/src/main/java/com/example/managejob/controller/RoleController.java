package com.example.managejob.controller;

import com.example.managejob.dto.RoleUserDTO;
import com.example.managejob.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("role")
public class RoleController {
    @Autowired
    RoleService roleService;

    @GetMapping("/add")
    public String add() {
        return "admin/role/add";
    }

    @PostMapping("/add")
    public String add(@RequestParam("role") String role, Model model) {
        roleService.addPost(role, model);
        return "admin/role/add";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model) {
        roleService.editGet(id, model);
        return "admin/role/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute RoleUserDTO roleUserDTO, Model model) {
        roleService.editPost(roleUserDTO, model);
        return "redirect:/role/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        roleService.delete(id);
        return "redirect:/role/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        roleService.getList(model, page);
        return "admin/role/list";
    }
}
