package com.example.managejob.service.impl;

import com.example.managejob.controller.UserController;
import com.example.managejob.model.Task;
import com.example.managejob.model.User;
import com.example.managejob.repository.RoleRepository;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.TaskRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.UserExcelExporter;
import com.example.managejob.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    HttpSession session;

    @Override
    public void processOAuthPostLogin(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            user.setAvatar("20190913_224545.jpg");
            user.setEmail(email);
            user.setName(email);
            user.setCreatedAt(new Date());
            user.setModifyBy(email);
            user.setRoleUser(roleRepository.findByRole("ROLE_MEMBER"));
        }
        session.setAttribute("emailUser", email);
    }

    @Override
    public void account(Principal principal, Model model) {
        logger.info("Thong tin account: username = " + userRepository.findById((int) session.getAttribute("idCurrentUser")).orElse(null).getName());

        int id = (int) session.getAttribute("idCurrentUser");

        List<Task> listTask = taskRepository.findListByStatus("Todo List", id);
        int count = listTask.size();

        List<Task> listTaskInProgress = taskRepository.findListByStatus("In Progress", id);
        int countI = listTaskInProgress.size();

        List<Task> listTaskReview = taskRepository.findListByStatus("Review", id);
        int countR = listTaskReview.size();

        List<Task> listTaskDone = taskRepository.findListByStatus("Done", id);
        int countD = listTaskDone.size();

        List<Task> listTaskCancel = taskRepository.findListByStatus("Cancel", id);
        int countC = listTaskCancel.size();

        List<Task> listTaskExpired = taskRepository.checkEndDate(id);
        int countE = listTaskExpired.size();

        List<Task> listAll = taskRepository.findListUserById(id);
        int countAll = listAll.size();

        model.addAttribute("count", count);
        model.addAttribute("countI", countI);
        model.addAttribute("countR", countR);
        model.addAttribute("countD", countD);
        model.addAttribute("countC", countC);
        model.addAttribute("countE", countE);
        model.addAttribute("countAll", countAll);

        User user = userRepository.findById((int) session.getAttribute("idCurrentUser")).orElse(null);
        model.addAttribute("user", user);
    }

    @Override
    public void addPost(User user, Principal principal, Model model) throws IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[^{]{4,150}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,150}$");
        Matcher m1 = pattern1.matcher(user.getPassword());

        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(user.getEmail());

        User userCheck = userRepository.findByName(user.getName());

        User emailCheck = userRepository.findByEmail(user.getEmail());

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
            model.addAttribute("roleList", roleRepository.findAll());
        } else {
            if (!user.getFile().isEmpty()) {
                final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
                String filename = user.getFile().getOriginalFilename();
                File newFile = new File(UPLOAD_FOLDER + filename);
                user.getFile().transferTo(newFile);
                user.setAvatar(filename); // save to db
            }
            user.setModifyBy(principal.getName());
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setCreatedAt(new Date(System.currentTimeMillis()));
            user.setRoleUser(user.getRoleUser());
            userRepository.save(user);
            model.addAttribute("success", "Insert Successfully");
        }
    }


    @Override
    public String editGet(int id, Model model) {
        session.setAttribute("idEditUser", id);
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("roleList", roleRepository.findAll());
        return "admin/user/edit";
    }

    @Override
    public void editPost(User user, Principal principal, Model model)
            throws IllegalStateException, IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[^{]{4,150}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,}$");
        Matcher m1 = pattern1.matcher(user.getPassword());

        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(user.getEmail());

        User userCheck = userRepository.findByName(user.getName());

        User emailCheck = userRepository.findByEmail(user.getEmail());

        if (userCheck != null && userCheck.getId() != (int) session.getAttribute("idEditUser")) {
            model.addAttribute("errName", "Duplicate username");
            k = 1;
        }

        if (emailCheck != null && emailCheck.getId() != (int) session.getAttribute("idEditUser")) {
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
            model.addAttribute("roleList", roleRepository.findAll());

        } else {
            User current = userRepository.findById((int) session.getAttribute("idEditUser")).orElse(null);
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
                model.addAttribute("success", "Insert Successfully");
                userRepository.save(current);
            }
        }
    }

    @Override
    public void getList(Model model, Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 3;
        Pageable pageagle = PageRequest.of(page, size);
        Page<User> pageUser = userRepository.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", userRepository.count());
    }

    @Override
    public void search(String name, Model model, Integer page) {
        page = page == null || page < 0 ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<User> pageUser = userRepository.searchByName(name, pageagle);
        if (name.equalsIgnoreCase("")) {
            pageUser = userRepository.findAll(pageagle);
        }
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPages", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", userRepository.count());
    }

    @Override
    public void download(String filename, HttpServletResponse response) throws IOException {
        final String UPLOAD_FOLDER = "D:/file/qlcv/user/";
        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @Override
    public void exportData(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        String currentDateTime = dateFormat.format(new Date());
        String fileName = "users_" + currentDateTime + ".xlsx";
        String headerValue = "attachement; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<User> listUsers = userRepository.findAll();
        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);
        excelExporter.export(response);
    }


    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public void accountById(Model model, int id) {
        User user = userRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
    }
}
