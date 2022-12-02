package com.example.managejob.controller;

import com.example.managejob.model.User;
import com.example.managejob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    UserRepository ur;

    @GetMapping("add")
    public String add() {
        return "admin/user/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute User user, Principal principal, Model model, HttpSession session) throws IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]{4,}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,}$");
        Matcher m1 = pattern1.matcher(user.getPassword());

        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(user.getEmail());

        User userCheck = ur.findByName(user.getName());

        User emailCheck = ur.findByEmail(user.getEmail());

        if (userCheck != null) {
            model.addAttribute("errName", "Duplicate username");
            k = 1;
        }

        if (emailCheck != null) {
            model.addAttribute("errEmail", "Duplicate email");
            k = 1;
        }

        if (!m.find()) {
            model.addAttribute("errName", "Enter length greater than 4 characters");
            k = 1;
        }

        if (!m1.find()) {
            model.addAttribute("errPass", "Enter length greater than 4 characters");
            k = 1;
        }

        if (!m2.find()) {
            model.addAttribute("errEmail", "Wrong format(VD: A@gmail.com)");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("pass", user.getPassword());
            return "admin/user/add";
        } else {
            if (!user.getFile().isEmpty()) {
                final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
                String filename = user.getFile().getOriginalFilename();
                File newFile = new File(UPLOAD_FOLDER + filename);
                user.getFile().transferTo(newFile);
                user.setAvatar(filename); // save to db
            }
            user.setModifyBy(principal.getName());
            user.setRole("ROLE_MEMBER");
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setCreatedAt(new Date(System.currentTimeMillis()));
            ur.save(user);
        }
        return "redirect:/user/list";
    }

    @RequestMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
        final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model, HttpSession session) {
        session.setAttribute("idEditUser", id);
        User user = ur.findById(id).orElse(null);
        model.addAttribute("user", user);

        return "admin/user/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("user") User user, HttpSession session, Principal principal)
            throws IllegalStateException, IOException {
        user.setCreatedAt(new Date(System.currentTimeMillis()));
        User current = ur.findById((int) session.getAttribute("idEditUser")).orElse(null);

        if (current != null) {
            // lay du lieu can update tu edit qua current, de tranh mat du lieu cu
            current.setName(user.getName());
            current.setEmail(user.getEmail());
            current.setModifyBy(principal.getName());
            current.setCreatedAt(new Date(System.currentTimeMillis()));
            current.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            if (!user.getFile().isEmpty()) {
                final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
                String filename = user.getFile().getOriginalFilename();
                File newFile = new File(UPLOAD_FOLDER + filename);
                user.getFile().transferTo(newFile);
                current.setAvatar(filename); // save to db
            }
            ur.save(current);
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        ur.deleteById(id);
        return "redirect:/user/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<User> pageUser = ur.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = ur.count();
        model.addAttribute("count", count);
        return "admin/user/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam("searchName") String name, Model model,
                         @RequestParam(value = "page", required = false) Integer page) {
        page = page == null || page < 0 ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<User> pageUser = ur.searchByName(name, pageagle);
        if (name.equalsIgnoreCase("")) {
            pageUser = ur.findAll(pageagle);
        }
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPages", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = ur.count();
        model.addAttribute("count", count);
        return "admin/user/list";
    }
}

