package com.example.managejob.service;

import com.example.managejob.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

public interface UserService {
    void processOAuthPostLogin(String email);

    void account(Principal principal, Model model);

    void addPost(User user, Principal principal, Model model) throws IOException;

    String editGet(int id, Model model);

    void editPost(User user, Principal principal, Model model)
            throws IllegalStateException, IOException;

    String editProfilePost(User user, Principal principal, Model model)
            throws IllegalStateException, IOException;

    void getList(Model model, Integer page);

    void search(String name, Model model, Integer page);

    void download(String filename, HttpServletResponse response) throws IOException;

    void exportData(HttpServletResponse response) throws IOException;

    User findByName(String name);

    void saveUser(User user);

    User findByEmail(String email);

    User findById(int id);

    void delete(int id);

    void accountById(Model model, int id);
}
