package com.example.managejob.controller;

import com.example.managejob.dto.GroupUserDTO;
import com.example.managejob.repository.GroupUserRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("group")
public class GroupUserController {
    @Autowired
    GroupUserRepository groupUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupService groupService;

    @GetMapping("/viewGroup")
    public String viewGroup(Model model, @RequestParam("id") int idGroup) {
        groupService.viewGroupUser(idGroup, model);
        return "admin/group/viewGroup";
    }

    @GetMapping("/viewGroupUser")
    public String viewGroupUser(Model model, @RequestParam("id") int idGroup) {
        groupService.viewGroupUser(idGroup, model);
        return "admin/group/viewGroupUser";
    }

    @GetMapping("/add")
    public String add() {
        return "admin/group/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("groupUser") GroupUserDTO groupUserDTO, Principal principal) {
        groupService.addPost(groupUserDTO, principal);
        return "redirect:/group/list";
    }

    @GetMapping("/edit")
    public String editGet(Model model, @RequestParam("id") int id) {
        groupService.editGet(id, model);
        return "admin/group/edit";
    }

    @PostMapping("/edit")
    public String editPost(@ModelAttribute("groupUser") GroupUserDTO groupUserDTO, Principal principal, HttpSession session, Model model) {
        groupService.editPost(groupUserDTO, model, principal);
        return "redirect:/group/edit?id=" + (int) session.getAttribute("idGroupEdit");
    }

    @GetMapping("/addMember")
    public String addMember(@RequestParam(name = "id", required = false) Integer id, Model model) {
        groupService.addMember(id, model);
        return "admin/group/addMember";
    }

    @PostMapping("/addMember")
    public String addMember(@RequestParam(name = "emailName") String emailName, Model model) {
        groupService.addMemberPost(emailName, model);
        return "admin/group/addMember";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        groupService.deleteById(id);
        return "redirect:/group/list";
    }

    @GetMapping("/groupUser")
    public String groupUser(Model model, @RequestParam(value = "page", required = false) Integer page, Principal principal) {
        groupService.groupUser(page, principal, model);
        return "admin/group/groupUser";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        groupService.list(page, model);
        return "admin/group/list";
    }

    @GetMapping("/search")
    public String searchByUser(Model model,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "nameGroup", required = false) String nameGroup,
                               @RequestParam(value = "description", required = false) String description) {
        groupService.searchByUser(model, page, nameGroup, description);
        return "admin/group/groupUser";
    }

    @GetMapping("/leaveGroup")
    public String leaveGroup(@RequestParam("name") String name, HttpSession session) {
        groupService.leaveGroup(name);
        int idCurrentGroup = (int) session.getAttribute("idGroup");
        return "redirect:/group/viewGroup?id=" + idCurrentGroup;
    }

    @GetMapping("/leaveGroupUser")
    public String leaveGroupUser() {
        groupService.leaveGroupUser();
        return "redirect:/group/groupUser";
    }
}
