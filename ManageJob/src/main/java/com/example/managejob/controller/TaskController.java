package com.example.managejob.controller;

import com.example.managejob.dto.TaskDTO;
import com.example.managejob.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;

@Controller
@RequestMapping("task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping("/view")
    public String viewGet(Model model, @RequestParam(value = "id", required = false) Integer id) {
        taskService.viewGet(id, model);
        return "admin/task/view";
    }

    @PostMapping("/view")
    public String view(@RequestParam(value = "content", required = false) String content,
                       @RequestParam(value = "document", required = false) MultipartFile multipartFile,
                       Model model, HttpSession session, Principal principal) {
        String id1 = (String) session.getAttribute("idTask");
        taskService.view(content, principal, multipartFile, model);
        return "redirect:/task/view?id=" + id1;
    }

    @GetMapping("/deleteFile")
    public String deleteFile(@RequestParam(value = "idComment", required = false) Integer idComment,
                             @RequestParam(value = "idDocument", required = false) Integer idDocument) {
        taskService.deleteFile(idDocument, idComment);
        return "redirect:/task/view";
    }

    @GetMapping("/download")
    public void download(@RequestParam("id") int id, HttpServletResponse response) {
        taskService.download(id, response);
    }

    @GetMapping("/add")
    public String addGet(Model model) {
        taskService.addGet(model);
        return "admin/task/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute TaskDTO taskDTO, Model model, Principal principal){
        taskService.createTask(taskDTO, model, principal);
        return "admin/task/add";
    }

    @GetMapping("/edit")
    public String editGet(@RequestParam("id") int id, Model model) {
        taskService.editGet(id, model);
        return "admin/task/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("task") TaskDTO taskDTO, Model model, Principal principal) throws IllegalStateException {
        taskService.edit(model, taskDTO, principal);
        return "redirect:/task/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        taskService.delete(id);
        return "redirect:/task/list";
    }

    @GetMapping("/myTask")
    public String myTask(Model model, @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "pageToDo", required = false) Integer pageToDo) {
        taskService.myTask(page, model, pageToDo);
        return "admin/task/taskUser";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        taskService.getList(page, model);
        return "admin/task/list";
    }

    @GetMapping("/viewTaskUserGroup")
    public String viewTaskUserGroup(Model model,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "pageToDo", required = false) Integer pageToDo) {
        taskService.viewTaskUserGroup(page, pageToDo, model, name);
        return "admin/task/viewTaskUserGroup";
    }

    @GetMapping("/listByStatus")
    public String listByStatus(Model model, @RequestParam(value = "page", required = false) Integer page, @RequestParam("status1") String status1) {
        taskService.listByStatus(page, model, status1);
        return "admin/task/list";
    }

    @GetMapping("/listByStatusId")
    public String listByStatusId(Model model, @RequestParam(value = "page", required = false) Integer page, @RequestParam("status1") String status1) {
        taskService.listByStatusId(page, model, status1);
        return "admin/task/taskUser";
    }

    @GetMapping("/searchTaskUser")
    public String searchTaskUser(Model model,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "group", required = false) String group,
                                 @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                 @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        taskService.searchTaskUser(page, name, group, startDate, endDate, model);
        return "admin/task/taskSearch";
    }

    @GetMapping("/exportExcel")
    public void exportData(HttpServletResponse response) {
        taskService.exportData(response);
    }

    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam("id") int id) {
        taskService.changeStatus(id);
        return "redirect:/task/myTask";
    }

}
