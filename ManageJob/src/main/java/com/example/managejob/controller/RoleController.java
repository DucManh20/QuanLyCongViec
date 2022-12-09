package com.example.managejob.controller;

import com.example.managejob.model.RoleUser;
import com.example.managejob.model.Status;
import com.example.managejob.repository.RoleRepository;
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
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleRepository rr;

    @GetMapping("/add")
    public String add() {
        return "admin/role/add";
    }

    @PostMapping("/add")
    public String add(@RequestParam("role") String role, Model model) {
        int k = 0;
        Pattern statusP = Pattern.compile("^[0-9a-zA-Z]{1,30}$");
        Matcher m2 = statusP.matcher(role);

        RoleUser roleCheck = rr.findByRole(role);

        if (roleCheck != null) {
            model.addAttribute("errRole", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errRole", "Not empty and length < 30");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("role", role);
            return "admin/role/add";
        }
        RoleUser roleUser = new RoleUser();
        roleUser.setRole(role);
        rr.save(roleUser);
        return "redirect:/role/list";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model, HttpSession session) {
        RoleUser roleUser = rr.findById(id).orElse(null);
        model.addAttribute("roleUser", roleUser);
        session.setAttribute("idRole", id);
        return "admin/role/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute RoleUser role, Model model, HttpSession session) {
        int k = 0;
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]{1,30}$");
        Matcher m2 = pattern.matcher(role.getRole());
        RoleUser roleCheck = rr.findByRole(role.getRole());

        if (roleCheck != null && roleCheck.getId() != (int) session.getAttribute("idRole")) {
            model.addAttribute("errRole", "Duplicate role");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errRole", "Not empty and length < 30");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("role", role.getRole());
            return "admin/role/edit";
        } else {
            role.setId((int) session.getAttribute("idRole"));
            rr.save(role);
        }

        return "redirect:/role/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        rr.deleteById(id);
        return "redirect:/role/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<RoleUser> pageUser = rr.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = rr.count();
        model.addAttribute("count", count);
        return "admin/role/list";
    }
}
