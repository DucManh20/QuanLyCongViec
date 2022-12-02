package com.example.managejob.controller;

import com.example.managejob.model.Comment;
import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import com.example.managejob.repository.CommentRepository;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.TaskRepository;
import com.example.managejob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("task")
public class TaskController {
    @Autowired
    TaskRepository tr;

    @Autowired
    UserRepository ur;

    @Autowired
    StatusRepository sr;

    @Autowired
    CommentRepository cr;

    @GetMapping("/view")
    public String view(Model model, @RequestParam("id") int id, HttpSession session) {
        Task task = tr.findById(id).orElse(null);
        model.addAttribute("task", task);

        String idTask = String.valueOf(id);
       session.setAttribute("idTask", idTask);
        model.addAttribute("commentList", cr.findAll());
        model.addAttribute("listC", cr.searchByTaskId(id));

        return "admin/task/view";
    }

    @PostMapping("/view")
    public String view(@RequestParam("content") String content, Model model, HttpSession session, Principal principal) {
        String id1 = (String)session.getAttribute("idTask");
        int id2 = Integer.parseInt((String)session.getAttribute("idTask")) ;
        Task task = tr.findById(id2).orElse(null);
        Comment c = new Comment();
        c.setContent(content);
        c.setTask(task);
        c.setUser(ur.findByName(principal.getName()));
        c.setCreatedAt(new Date(System.currentTimeMillis()));
        cr.save(c);
        model.addAttribute("listC", cr.searchByTaskId(id2));

        return "redirect:/task/view?id="+id1;
    }


    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("statusList", sr.findAll());
        model.addAttribute("userList", ur.findAll());
        return "admin/task/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Task task, Principal principal, Model model, HttpSession session) throws IOException {
        tr.save(task);
        System.err.println(task);

        return "admin/task/add";
    }


//    @GetMapping("/edit")
//    public String edit(@RequestParam("id") int id, Model model) {
//        User user = tr.findById(id).orElse(null);
//        model.addAttribute("user", user);
//        return "admin/user/edit";
//    }

//    @PostMapping("/edit")
//    public String edit(@ModelAttribute("user") User user)
//            throws IllegalStateException, IOException {
//        user.setCreatedAt(new Date(System.currentTimeMillis()));
//        User current = tr.findById(user.getId()).orElse(null);
//
//        if (current != null) {
//            // lay du lieu can update tu edit qua current, de tranh mat du lieu cu
//            current.setName(user.getName());
//            current.setEmail(user.getEmail());
//            if (!user.getFile().isEmpty()) {
//                final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
//                String filename = user.getFile().getOriginalFilename();
//                File newFile = new File(UPLOAD_FOLDER + filename);
//                user.getFile().transferTo(newFile);
//                current.setAvatar(filename); // save to db
//            }
//            tr.save(current);
//        }
//        return "redirect:/task/list";
//    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        tr.deleteById(id);
        return "redirect:/task/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("listD", pageUser);
        long count = tr.count();
        model.addAttribute("count", count);
        return "admin/task/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam("name") String name, Model model,
                         @RequestParam(value = "page", required = false) Integer page) {
        page = page == null || page < 0 ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.searchByName(name, pageagle);
        if (name.equalsIgnoreCase("")) {
            pageUser = tr.findAll(pageagle);
        }
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPages", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("listD", pageUser);
        long count = tr.count();
        model.addAttribute("count", count);
        return "admin/task/list";
    }
}
