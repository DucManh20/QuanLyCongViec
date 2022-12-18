package com.example.managejob.controller;

import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import com.example.managejob.service.UserExcelExporter;
import com.example.managejob.repository.RoleRepository;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.TaskRepository;
import com.example.managejob.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("user")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class UserController {

    private  static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserRepository ur;

    @Autowired
    TaskRepository tr;

    @Autowired
    StatusRepository sr;

    @Autowired
    RoleRepository rr;

    @GetMapping("add")
    public String add(Model model) {
        model.addAttribute("roleList", rr.findAll());

        return "admin/user/add";
    }

    @GetMapping("/account")
    public String account(Principal principal, Model model, HttpSession session){
        logger.info("Thong tin account: username = " + principal.getName());

        int id =(int) session.getAttribute("idCurrentUser");

        List<Task> listTask = tr.findListByStatus("Todo List", id);
        int count = listTask.size();

        List<Task> listTaskInProgress = tr.findListByStatus("In Progress", id);
        int countI = listTaskInProgress.size();

        List<Task> listTaskReview = tr.findListByStatus("Review", id);
        int countR = listTaskReview.size();

        List<Task> listTaskDone = tr.findListByStatus("Done", id);
        int countD = listTaskDone.size();

        List<Task> listAll = tr.findListUserById(id);
        int countAll = listAll.size();

        model.addAttribute("count", count);
        model.addAttribute("countI", countI);
        model.addAttribute("countR", countR);
        model.addAttribute("countD", countD);
        model.addAttribute("countAll", countAll);

        User user = ur.findByName(principal.getName());
        model.addAttribute("user", user);
        return "admin/user/account";
    }

    @GetMapping("/accountById")
    public String accountById(Principal principal, Model model, @RequestParam("id") int id){
        User user = ur.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "admin/user/account";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute User user, Principal principal, Model model, HttpSession session) throws IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[^{]{4,150}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,150}$");
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
            model.addAttribute("errName", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (!m1.find()) {
            model.addAttribute("errPass", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (!m2.find()) {
            model.addAttribute("errEmail", "Wrong format (EX: A@gmail.com)");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("pass", user.getPassword());
            model.addAttribute("roleList", rr.findAll());
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
//            user.setRole("ROLE_MEMBER");
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setCreatedAt(new Date(System.currentTimeMillis()));
//            user.setRoleUser(user.getRoleUser());
            System.err.println(user.getRoleUser().getRole());
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

    @GetMapping("/exportExcel")
    public void exportData(HttpServletResponse response) throws IOException{
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        String currentDateTime = dateFormat.format(new Date());
        String fileName = "users_" + currentDateTime + ".xlsx";
        String headerValue = "attachement; filename="+ fileName;
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = ur.findAll();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);
        excelExporter.export(response);

    }
    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model, HttpSession session) {
        session.setAttribute("idEditUser", id);
        User user = ur.findById(id).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("roleList", rr.findAll());
        return "admin/user/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("user") User user, HttpSession session, Principal principal, Model model)
            throws IllegalStateException, IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[^{]{4,150}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,}$");
        Matcher m1 = pattern1.matcher(user.getPassword());

        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(user.getEmail());

        User userCheck = ur.findByName(user.getName());

        User emailCheck = ur.findByEmail(user.getEmail());

        if (userCheck != null && userCheck.getId() != (int)session.getAttribute("idEditUser")) {
            model.addAttribute("errName", "Duplicate username");
            k = 1;
        }

        if (emailCheck != null && emailCheck.getId() != (int)session.getAttribute("idEditUser")) {
            model.addAttribute("errEmail", "Duplicate email");
            k = 1;
        }

        if (!m.find()) {
            model.addAttribute("errName", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (!m1.find()) {
            model.addAttribute("errPass", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (!m2.find()) {
            model.addAttribute("errEmail", "Wrong format (EX: A@gmail.com)");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("pass", user.getPassword());
            model.addAttribute("roleList", rr.findAll());
            return "admin/user/add";
        } else {
            User current = ur.findById((int) session.getAttribute("idEditUser")).orElse(null);
            if (current != null) {
                // lay du lieu can update tu edit qua current, de tranh mat du lieu cu
                current.setId((int) session.getAttribute("idEditUser"));
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

