package com.example.managejob.service;

import com.example.managejob.dto.GroupUserDTO;
import org.springframework.ui.Model;

import java.security.Principal;

public interface GroupService {

    void viewGroupUser(int idGroup, Model model);

    void addPost(GroupUserDTO groupUserDTO, Principal principal);

    void editGet(int id, Model model);

    void editPost(GroupUserDTO groupUserDTO, Model model, Principal principal);

    void addMember(Integer id, Model model);

    void addMemberPost(String emailName, Model model);

    void deleteById(int id);

    void groupUser(Integer page, Principal principal, Model model);

    void list(Integer page, Model model);

    void searchByUser(Model model, Integer page, String nameGroup, String description);

    void leaveGroup(String name);

    void leaveGroupUser();
}
