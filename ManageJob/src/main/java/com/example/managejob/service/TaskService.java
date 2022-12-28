package com.example.managejob.service;

import com.example.managejob.dto.TaskDTO;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Date;

public interface TaskService {
    void  createTask(TaskDTO taskDTO, Model model, Principal principal);

    void searchTaskUser(Integer page, String name, String group, Date startDate, Date endDate, Model model);

    void listByStatus(Integer page, Model model, String status1);

    void listByStatusId(Integer page, Model model, String status1);

    void view(String content, Principal principal, MultipartFile multipartFile, Model model);

    void exportData(HttpServletResponse response);

    void getList(Integer page, Model model);

    void myTask(Integer page, Model model, Integer pageToDo);

    void viewTaskUserGroup(Integer page, Integer pageToDo, Model model, String name);

    void delete(int id);

    void edit(Model model, TaskDTO taskDTO, Principal principal);

    void editGet(int id, Model model);

    void download(int id, HttpServletResponse response);

    void viewGet(Integer id , Model model);

    void deleteFile(Integer idDocument, Integer idComment);

    void addGet(Model model);

    void changeStatus(int id);
}
