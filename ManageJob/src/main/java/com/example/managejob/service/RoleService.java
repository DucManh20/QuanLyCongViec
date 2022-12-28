package com.example.managejob.service;

import com.example.managejob.dto.RoleUserDTO;
import com.example.managejob.model.RoleUser;
import org.springframework.ui.Model;
import java.util.List;

public interface RoleService {
    void addPost(String role, Model model);

    void editGet(int id, Model model);

    void editPost(RoleUserDTO roleUserDTO, Model model);

    void delete(int id);

    void getList(Model model, Integer page);

    List<RoleUser> findAll();
}
