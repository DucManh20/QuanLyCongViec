package com.example.managejob.controller;

import com.example.managejob.dto.StatusDTO;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.service.StatusService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("status")
public class StatusController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    StatusService statusService;

    @Autowired
    StatusRepository statusRepository;

    @GetMapping("/add")
    public String add() {
        return "admin/status/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("status") StatusDTO statusDTO, Model model) {
        statusService.createStatus(statusDTO, model);
        return "admin/status/add";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam(value = "id", required = false) Integer id, Model model) {
        if (id != null) {
            statusService.getStatusById(id, model);
        }
        return "admin/status/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute StatusDTO statusDTO, Model model) {
        statusService.updateStatus(statusDTO, model);
        return "admin/status/edit";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        statusService.deleteStatus(id);
        return "redirect:/status/list";
    }

    @GetMapping("/list")
    public String list(@RequestParam(value = "page", required = false) Integer page, Model model) {
        statusService.getAllStatus(page, model);
        return "admin/status/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "nameStatus", required = false) String nameStatus, Model model) {
        statusService.findByName(nameStatus, model, page);
        return "admin/status/list";
    }
}
