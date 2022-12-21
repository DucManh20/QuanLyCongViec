package com.example.managejob.controller;

import com.example.managejob.model.GroupUser;
import com.example.managejob.model.User;
import com.example.managejob.repository.GroupUserRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.GroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("group")
public class GroupUserController {
    @Autowired
    GroupUserRepository groupUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupUserService groupUserService;

    @GetMapping("/viewGroup")
    public String viewGroup(Model model, @RequestParam("id") int idGroup, HttpSession session) {
        session.setAttribute("idGroup", idGroup);
        GroupUser groupUser = groupUserRepository.findById(idGroup).orElse(null);
        model.addAttribute("group", groupUser);

        List<User> listUser = groupUser.getUsers();
        model.addAttribute("listUser", listUser);
        return "admin/group/viewGroup";
    }

    @GetMapping("/viewGroupUser")
    public String viewGroupUser(Model model, @RequestParam("id") int idGroup, HttpSession session) {
        session.setAttribute("idGroup", idGroup);
        GroupUser groupUser = groupUserRepository.findById(idGroup).orElse(null);
        model.addAttribute("group", groupUser);
        List<User> listUser = groupUser.getUsers();
        model.addAttribute("listUser", listUser);
        return "admin/group/viewGroupUser";
    }

    @GetMapping("/add")
    public String add(){
        return "admin/group/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("groupUser") GroupUser groupUser, Principal principal){
        groupUser.setCreatedAt(new Date(System.currentTimeMillis()));
        groupUser.setCreatedBy(principal.getName());
        groupUserRepository.save(groupUser);
        return "redirect:/group/list";
    }

    @GetMapping("/edit")
    public String edit(Model model, @RequestParam("id") int id, HttpSession session){
        GroupUser groupUser = groupUserRepository.findById(id).orElse(null);
        session.setAttribute("idGroupEdit", id);
        model.addAttribute("group", groupUser);
        return "admin/group/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("groupUser") GroupUser groupUser, Principal principal, HttpSession session, Model model){
        GroupUser groupUser1 = groupUserRepository.findByName(groupUser.getName());

        if(groupUser1 != null && groupUser1.getId() != (int) session.getAttribute("idGroupEdit")){
            model.addAttribute("errName", "Duplicate!!");
            return "redirect:/group/edit?id="+(int) session.getAttribute("idGroupEdit");
        }
        groupUser.setId((int) session.getAttribute("idGroupEdit"));
        groupUser.setCreatedAt(new Date(System.currentTimeMillis()));
        groupUser.setCreatedBy(principal.getName());
        groupUserRepository.save(groupUser);
        return "redirect:/group/list";
    }

    @GetMapping("/addMember")
    public String addMember(@RequestParam(name = "id", required = false) Integer id, HttpSession session) {
        if(id != null ){
            session.setAttribute("groupId", id);
        }
        return "admin/group/addMember";
    }

    @PostMapping("/addMember")
    public String addMember(@RequestParam(name = "emailName") String emailName,
                            Model model, HttpSession session){
        User user = null;
        if(emailName.contains("@")){
            user = userRepository.findByEmail(emailName);
        }else {
            user = userRepository.findByName(emailName);
        }

        if(user != null){

            GroupUser groupUser = groupUserRepository.findById((int)session.getAttribute("groupId")).orElse(null);
            List<User> ds = groupUser.getUsers();
            ds.add(user);
            groupUser.setUsers(ds);
            groupUserRepository.save(groupUser);
        }else{
            String errEmailUser = "User not found!";
            model.addAttribute("errEmailUser", errEmailUser);
            return  "admin/group/addMember";
        }
        return "redirect:/group/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        groupUserRepository.deleteById(id);
        return "redirect:/group/list";
    }

    @GetMapping("/groupUser")
    public String groupUser(Model model, @RequestParam(value = "page", required = false) Integer page, Principal principal) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 20;
        User user = userRepository.findByName(principal.getName());

        List<GroupUser> groups = user.getGroups();
        Pageable pageagle = PageRequest.of(page, size);
        Page<GroupUser> groupUserPage = groupUserRepository.findAll(pageagle);
        model.addAttribute("listGroupUser", groups);
        model.addAttribute("totalPage", groupUserPage.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = groups.size();
        model.addAttribute("count", count);
        return "admin/group/groupUser";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
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
        return "admin/group/list";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "nameGroup", required = false) String nameGroup,
                         @RequestParam(value = "description", required = false) String description) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<GroupUser> groupUserPage = null;
        if(nameGroup == null && description == null){
            groupUserPage = groupUserRepository.findAll(pageagle);
        }else{
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
        return "admin/group/list";
    }

    @GetMapping("/leaveGroup")
    public String leaveGroup(@RequestParam("name") String name, Model model, HttpSession session){
        int idCurrentGroup = (int)session.getAttribute("idGroup");
        GroupUser groupUser = groupUserRepository.findById(idCurrentGroup).orElse(null);
        List<User> listUser =  groupUser.getUsers();
        listUser.remove(userRepository.findByName(name));
        groupUser.setUsers(listUser);
        groupUserRepository.save(groupUser);
        return "redirect:/group/viewGroup?id=" + idCurrentGroup;
    }

    @GetMapping("/leaveGroupUser")
    public String leaveGroupUser(HttpSession session){
        int idCurrentGroup = (int)session.getAttribute("idGroup");
        String nameCurrentUser = (String)session.getAttribute("currentUser");
        GroupUser groupUser = groupUserRepository.findById(idCurrentGroup).orElse(null);
        List<User> listUser =  groupUser.getUsers();
        listUser.remove(userRepository.findByName(nameCurrentUser));
        groupUser.setUsers(listUser);
        groupUserRepository.save(groupUser);
        return "redirect:/group/groupUser";
    }
}
