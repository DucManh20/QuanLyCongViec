package com.example.managejob.service.impl;

import com.example.managejob.dto.GroupUserDTO;
import com.example.managejob.model.GroupUser;
import com.example.managejob.model.User;
import com.example.managejob.repository.GroupUserRepository;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.GroupService;
import com.example.managejob.utils.ConvertListToPage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    HttpSession session;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    GroupUserRepository groupUserRepository;


    @Override
    public void viewGroupUser(int idGroup, Model model) {
        try {
            session.setAttribute("idGroup", idGroup);
            GroupUser groupUser = groupUserRepository.findById(idGroup).orElse(null);
            model.addAttribute("group", groupUser);
            List<User> listUser = groupUser.getUsers();
            model.addAttribute("listUser", listUser);
            model.addAttribute("totalMember", listUser.size());
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void addPost(GroupUserDTO groupUserDTO, Principal principal) {
        GroupUser groupUser = modelMapper.map(groupUserDTO, GroupUser.class);
        groupUser.setCreatedAt(new Date(System.currentTimeMillis()));
        groupUser.setCreatedBy(principal.getName());
        groupUserRepository.save(groupUser);
    }

    @Override
    public void editGet(int id, Model model) {
        GroupUser groupUser = groupUserRepository.findById(id).orElse(null);
        session.setAttribute("idGroupEdit", id);
        model.addAttribute("group", groupUser);
    }

    @Override
    public void editPost(GroupUserDTO groupUserDTO, Model model, Principal principal) {
        GroupUser groupUser = modelMapper.map(groupUserDTO, GroupUser.class);
        GroupUser groupUser1 = groupUserRepository.findByName(groupUserDTO.getName());
        if (groupUser1 != null && groupUser1.getId() != (int) session.getAttribute("idGroupEdit")) {
            model.addAttribute("errName", "Duplicate!!");
        } else {
            groupUser.setId((int) session.getAttribute("idGroupEdit"));
            groupUser.setCreatedAt(new Date(System.currentTimeMillis()));
            groupUser.setCreatedBy(principal.getName());
            groupUserRepository.save(groupUser);
            model.addAttribute("success", "Edit Success");
        }
    }

    @Override
    public void addMember(Integer id) {
        if (id != null) {
            session.setAttribute("groupId", id);
        }
    }

    @Override
    public void addMemberPost(String emailName, Model model) {
        User user;
        if (emailName.contains("@")) {
            user = userRepository.findByEmail(emailName);
        } else {
            user = userRepository.findByName(emailName);
        }
        if (user != null) {
            try {
                GroupUser groupUser = groupUserRepository.findById((int) session.getAttribute("groupId")).orElse(null);
                List<User> ds = groupUser.getUsers();
                ds.add(user);
                groupUser.setUsers(ds);
                groupUserRepository.save(groupUser);
                model.addAttribute("success", "Insert Successfully");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            String errEmailUser = "User not found!";
            model.addAttribute("errEmailUser", errEmailUser);
        }
    }

    @Override
    public void deleteById(int id) {
        groupUserRepository.deleteById(id);
    }

    @Override
    public void groupUser(Integer page, Principal principal, Model model) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 9;
        Pageable pageagle = PageRequest.of(page, size);
        User user = userRepository.findById((int) session.getAttribute("idCurrentUser")).orElse(null);
        Page<GroupUser> groupUserPage;
        List<GroupUserDTO> groups = user.getGroups().stream().map(group
                -> modelMapper.map(group, GroupUserDTO.class)).toList();
        groupUserPage = ConvertListToPage.toPage(groups, pageagle);
        model.addAttribute("listGroupUser", groupUserPage);
        model.addAttribute("totalPage", groupUserPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = groupUserPage.stream().count();
        model.addAttribute("count", count);
    }

    @Override
    public void list(Integer page, Model model) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<GroupUser> groupUserPage = groupUserRepository.findAll(pageagle);
        model.addAttribute("listGroupUser", groupUserPage.getContent());
        model.addAttribute("totalPage", groupUserPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = groupUserRepository.count();
        model.addAttribute("count", count);
    }

    @Override
    public void searchByUser(Model model, Integer page, String nameGroup, String description) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<GroupUser> groupUserPage;
        if (nameGroup == null && description == null) {
            groupUserPage = groupUserRepository.findAll(pageagle);
        } else {
            groupUserPage = groupUserRepository.search(nameGroup, description, pageagle);
        }
        model.addAttribute("listGroupUser", groupUserPage.getContent());
        model.addAttribute("totalPage", groupUserPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = groupUserPage.getSize();
        model.addAttribute("count", count);
        model.addAttribute("nameGroup", nameGroup);
        model.addAttribute("description", description);
    }

    @Override
    public void leaveGroup(String name) {
        int idCurrentGroup = (int) session.getAttribute("idGroup");
        try {
            GroupUser groupUser = groupUserRepository.findById(idCurrentGroup).orElse(null);
            List<User> listUser = groupUser.getUsers();
            listUser.remove(userRepository.findByName(name));
            groupUser.setUsers(listUser);
            groupUserRepository.save(groupUser);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveGroupUser() {
        try {
            int idCurrentGroup = (int) session.getAttribute("idGroup");
            String nameCurrentUser = (String) session.getAttribute("currentUser");
            GroupUser groupUser = groupUserRepository.findById(idCurrentGroup).orElse(null);
            List<User> listUser = groupUser.getUsers();
            listUser.remove(userRepository.findByName(nameCurrentUser));
            groupUser.setUsers(listUser);
            groupUserRepository.save(groupUser);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
