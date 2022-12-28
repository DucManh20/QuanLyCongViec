package com.example.managejob.service.impl;

import com.example.managejob.dto.RoleUserDTO;
import com.example.managejob.model.RoleUser;
import com.example.managejob.repository.RoleRepository;
import com.example.managejob.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HttpSession session;

    @Override
    public void addPost(String role, Model model) {
        int k = 0;
        Pattern statusP = Pattern.compile("^[0-9a-zA-Z]{1,30}$");
        Matcher m2 = statusP.matcher(role);

        RoleUser roleCheck = roleRepository.findByRole(role);

        if (roleCheck != null) {
            model.addAttribute("errRole", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errRole", "Not empty and length < 30");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("role", role);
        }
        RoleUser roleUser = new RoleUser();
        roleUser.setRole(role);
        roleRepository.save(roleUser);
    }

    @Override
    public void editGet(int id, Model model) {
        RoleUser roleUser = roleRepository.findById(id).orElse(null);
        model.addAttribute("roleUser", roleUser);
        session.setAttribute("idRole", id);
    }

    @Override
    public void editPost(RoleUserDTO roleUserDTO, Model model) {
        RoleUser role = modelMapper.map(roleUserDTO, RoleUser.class);
        int k = 0;
        Pattern pattern = Pattern.compile("^[\\w]{1,30}$");
        Matcher m2 = pattern.matcher(role.getRole());
        RoleUser roleCheck = roleRepository.findByRole(role.getRole());

        if (roleCheck != null && roleCheck.getId() != (int) session.getAttribute("idRole")) {
            model.addAttribute("errRole", "Duplicate role");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errRole", "Not empty and length < 30");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("role", role.getRole());
        } else {
            role.setId((int) session.getAttribute("idRole"));
            roleRepository.save(role);
        }
    }

    @Override
    public void delete(int id) {
        roleRepository.deleteById(id);
    }

    @Override
    public void getList(Model model, Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        Page<RoleUser> pageUser = roleRepository.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", roleRepository.count());
    }

    @Override
    public List<RoleUser> findAll(){
        return roleRepository.findAll();
    }
}
