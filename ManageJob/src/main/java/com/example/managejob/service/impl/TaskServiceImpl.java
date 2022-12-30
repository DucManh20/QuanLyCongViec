package com.example.managejob.service.impl;

import com.example.managejob.dto.TaskDTO;
import com.example.managejob.model.Comment;
import com.example.managejob.model.Document;
import com.example.managejob.model.Status;
import com.example.managejob.model.Task;
import com.example.managejob.repository.*;
import com.example.managejob.service.TaskExcelExporter;
import com.example.managejob.service.TaskService;
import com.example.managejob.utils.ConvertListToPage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    HttpSession session;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GroupUserRepository groupUserRepository;

    @Override
    public void createTask(TaskDTO taskDTO, Model model, Principal principal) {
        Task task = modelMapper.map(taskDTO, Task.class);
        int k = 0;
        Pattern pattern = Pattern.compile("^[^{]{4,150}$");
        Matcher m = pattern.matcher(task.getName());
        Task taskCheck = taskRepository.findByName(task.getName());
        if (taskCheck != null) {
            model.addAttribute("errName", "Duplicate username");
            k = 1;
        }
        if (!m.find()) {
            model.addAttribute("errName", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("name", task.getName());
            model.addAttribute("spentTime", task.getSpentTime());
            model.addAttribute("estimateHourse", task.getEstimateHourse());
            model.addAttribute("statusList", statusRepository.findAll());
            model.addAttribute("userList", userRepository.findAll());
        } else {
            task.setModifyBy(principal.getName());
            taskRepository.save(task);
            model.addAttribute("success", "Insert Successfully");
            model.addAttribute("statusList", statusRepository.findAll());
            model.addAttribute("userList", userRepository.findAll());
            model.addAttribute("groupList", groupUserRepository.findAll());
        }
    }
    @Override
    public void searchTaskUser(Integer page, String name, String group, Date startDate, Date endDate, Model model) {
        page = page == null || page < 0 ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        int idCurrentUser = (int) session.getAttribute("idCurrentUser");
        Page<TaskDTO> pageTask;
        if (name == null && group == null && startDate == null && endDate == null) {
            List<TaskDTO> listD = taskRepository.findAll().stream().map(task
                    -> modelMapper.map(task, TaskDTO.class)).toList();
            pageTask = ConvertListToPage.toPage(listD, pageagle);
        } else {
            List<TaskDTO> listD = taskRepository.search(startDate, endDate, group, name, idCurrentUser, pageagle).stream().map(task
                    -> modelMapper.map(task, TaskDTO.class)).toList();
            pageTask = ConvertListToPage.toPage(listD, pageagle);
        }
        model.addAttribute("name", name);
        model.addAttribute("group", group);
        model.addAttribute("startDate", startDate);
        model.addAttribute("listD", pageTask.getContent());
        model.addAttribute("totalPages", pageTask.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("countPageTask", pageTask.getSize());
    }

    @Override
    public void listByStatus(Integer page, Model model, String status1) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        List<TaskDTO> listD = taskRepository.findListByStatus(status1, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageUser = ConvertListToPage.toPage(listD, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
    }

    @Override
    public void listByStatusId(Integer page, Model model, String status1) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        int id = (int) session.getAttribute("idCurrentUser");
        Pageable pageagle = PageRequest.of(page, size);
        List<TaskDTO> listD = taskRepository.findListByStatusId(status1, id, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageUser = ConvertListToPage.toPage(listD, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
    }

    @Override
    public void view(String content, Principal principal, MultipartFile multipartFile, Model model) {
        int id2 = Integer.parseInt((String) session.getAttribute("idTask"));
        Task task = taskRepository.findById(id2).orElse(null);
        if (content != null) {
            Comment c = new Comment();
            c.setContent(content);
            c.setTask(task);
            c.setUser(userRepository.findByName(principal.getName()));
            c.setCreatedAt(new Date(System.currentTimeMillis()));
            commentRepository.save(c);
        }
        if (multipartFile.getSize() != 0) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            Document d = new Document();
            d.setName(fileName);
            try {
                d.setContent(multipartFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            d.setSize((int) multipartFile.getSize());
            d.setUploadTime(new Date());
            d.setTask(task);
            d.setUser(userRepository.findByName(principal.getName()));
            documentRepository.save(d);
        }

        model.addAttribute("listDocument", documentRepository.searchByTaskId(id2));
        model.addAttribute("listC", commentRepository.searchByTaskId(id2));
    }

    @Override
    public void exportData(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        String currentDateTime = dateFormat.format(new Date());
        String fileName = "tasks_" + currentDateTime + ".xlsx";
        String headerValue = "attachement; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<Task> listTasks = taskRepository.findAll();
        TaskExcelExporter excelExporter = new TaskExcelExporter(listTasks);
        try {
            excelExporter.export(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getList(Integer page, Model model) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 7;
        Pageable pageagle = PageRequest.of(page, size);
        List<TaskDTO> listD = taskRepository.findAll().stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageUser = ConvertListToPage.toPage(listD, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("listD", pageUser);
        long count = taskRepository.count();
        model.addAttribute("count1", count);
    }
    @Override
    public void search(Model model, String name) {
        List<Task> listD = taskRepository.searchByName(name);
        model.addAttribute("listD", listD);
    }

    @Override
    public void myTask(Integer page, Model model, Integer pageToDo) {
        model.addAttribute("dateStart", "2000-01-01");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        model.addAttribute("dateEnd", date);

        int size = 7;
        //phan trang + get all list
        page = (page == null || page < 0) ? 0 : page;
        Pageable pageagle = PageRequest.of(page, size);
        int id = (int) session.getAttribute("idCurrentUser");
        List<TaskDTO> listTask = taskRepository.findListUserById(id).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageUser = ConvertListToPage.toPage(listTask, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count =pageUser.getSize() + 1;
        model.addAttribute("count", count);
        // phan trang + get Todo List
        pageToDo = (pageToDo == null || pageToDo < 0) ? 0 : pageToDo;
        Pageable pageagleToDo = PageRequest.of(pageToDo, size);
        Page<Task> pageTodoList = taskRepository.findListByStatusId("Todo List", id, pageagleToDo);
        model.addAttribute("listToDo", pageTodoList.getContent());
        model.addAttribute("pageToDo", pageToDo);
        model.addAttribute("totalPageToDo", pageTodoList.getTotalPages());

        List<TaskDTO> listReview = taskRepository.findListByStatusId("Review", id, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageReviewList = ConvertListToPage.toPage(listReview, pageagle);
        model.addAttribute("listReview", pageReviewList.getContent());

        List<TaskDTO> listDone = taskRepository.findListByStatusId("Done", id, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageDoneList = ConvertListToPage.toPage(listDone, pageagle);
        model.addAttribute("listDone", pageDoneList.getContent());

        List<TaskDTO> listInProgress = taskRepository.findListByStatusId("In progress", id, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageInProgressList = ConvertListToPage.toPage(listInProgress, pageagle);
        model.addAttribute("listInProgress", pageInProgressList.getContent());

        List<TaskDTO> listCancel = taskRepository.findListByStatusId("Cancel", id, pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageCancelList = ConvertListToPage.toPage(listCancel, pageagle);
        model.addAttribute("listCancel", pageCancelList.getContent());

        List<TaskDTO> listExpired = taskRepository.checkEndDate(id).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageExpired = ConvertListToPage.toPage(listExpired, pageagle);
        model.addAttribute("listExpired", pageExpired.getContent());
    }

    @Override
    public void viewTaskUserGroup(Integer page, Integer pageToDo, Model model, String name) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        //phan trang + get all list
        page = (page == null || page < 0) ? 0 : page;
        Pageable pageagle = PageRequest.of(page, size);
        int id = userRepository.findByName(name).getId();
        List<TaskDTO> listUser = taskRepository.findAllTaskByUserGroup(id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageUser = ConvertListToPage.toPage(listUser, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);

        // phan trang + get
        pageToDo = (pageToDo == null || pageToDo < 0) ? 0 : pageToDo;
        List<TaskDTO> listToDo = taskRepository.findListByUserGroup("Todo List", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageTodoList = ConvertListToPage.toPage(listToDo, pageagle);
        model.addAttribute("listToDo", pageTodoList.getContent());
        model.addAttribute("pageToDo", pageToDo);
        model.addAttribute("totalPageToDo", pageTodoList.getTotalPages());

        List<TaskDTO> listReview = taskRepository.findListByUserGroup("Review", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageReviewList = ConvertListToPage.toPage(listReview, pageagle);
        model.addAttribute("listReview", pageReviewList.getContent());

        List<TaskDTO> listDone = taskRepository.findListByUserGroup("Done", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageDoneList = ConvertListToPage.toPage(listDone, pageagle);
        model.addAttribute("listDone", pageDoneList.getContent());

        List<TaskDTO> listInProgress = taskRepository.findListByUserGroup("In Progress", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageInProgressList = ConvertListToPage.toPage(listInProgress, pageagle);
        model.addAttribute("listInProgress", pageInProgressList.getContent());

        List<TaskDTO> listCancel = taskRepository.findListByUserGroup("Cancel", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageCancelList = ConvertListToPage.toPage(listCancel, pageagle);
        model.addAttribute("listCancel", pageCancelList.getContent());

        List<TaskDTO> listExperid = taskRepository.findListByUserGroup("Experid", id, (int) session.getAttribute("idGroup"), pageagle).stream().map(task
                -> modelMapper.map(task, TaskDTO.class)).toList();
        Page<TaskDTO> pageExperidList = ConvertListToPage.toPage(listExperid, pageagle);
        model.addAttribute("listExperid", pageExperidList.getContent());
    }

    @Override
    public void delete(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public void edit(Model model, TaskDTO taskTDO, Principal principal) {
        Task task = modelMapper.map(taskTDO, Task.class);
        int k = 0;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]{4,150}$");
        Matcher m = pattern.matcher(task.getName());

        Task taskCheck = taskRepository.findByName(task.getName());

        if (taskCheck != null && taskCheck.getId() != (int) session.getAttribute("idEditTask")) {
            model.addAttribute("errName", "Duplicate username");
            k = 1;
        }

        if (!m.find()) {
            model.addAttribute("errName", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("name", task.getName());
            model.addAttribute("description", task.getDescription());
            model.addAttribute("startDate", task.getStartDate());
            model.addAttribute("endDate", task.getEndDate());
            model.addAttribute("statusList", statusRepository.findAll());
            model.addAttribute("userList", userRepository.findAll());
        } else {
            Task current = taskRepository.findById((int) session.getAttribute("idEditTask")).orElse(null);
            if (current != null) {
                // lay du lieu can update tu edit qua current, de tranh mat du lieu cu
                current.setId((int) session.getAttribute("idEditTask"));
                current.setName(task.getName());
                current.setStatus(task.getStatus());
                current.setDescription(task.getDescription());
                current.setStartDate(task.getStartDate());
                current.setEndDate(task.getEndDate());
                current.setModifyBy(principal.getName());
                current.setUser(task.getUser());
                taskRepository.save(current);
            }
            model.addAttribute("success", "Edit success");
        }

    }

    @Override
    public void editGet(int id, Model model) {
        session.setAttribute("idEditTask", id);
        Task task = taskRepository.findById(id).orElse(null);
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        model.addAttribute("task", taskDTO);
        model.addAttribute("statusList", statusRepository.findAll());
        model.addAttribute("userList", userRepository.findAll());
    }

    @Override
    public void download(int id, HttpServletResponse response) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document != null) {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + document.getName();
            response.setHeader(headerKey, headerValue);
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(document.getContent());
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void viewGet(Integer id, Model model) {
        int idCurrent;
        if (id != null) {
            idCurrent = id;
        } else {
            idCurrent = (int) session.getAttribute("idTaskI");
        }
        Task task = taskRepository.findById(idCurrent).orElse(null);
        model.addAttribute("task", task);
        String idTask = String.valueOf(idCurrent);
        session.setAttribute("idTask", idTask);
        session.setAttribute("idTaskI", idCurrent);
        model.addAttribute("listC", commentRepository.searchByTaskId(idCurrent));
        model.addAttribute("listDocument", documentRepository.searchByTaskId(idCurrent));
        model.addAttribute("listStatus", statusRepository.findAll());
    }

    @Override
    public void deleteFile(Integer idDocument, Integer idComment) {
        if (idDocument != null) {
            documentRepository.deleteById(idDocument);
        }
        if (idComment != null) {
            commentRepository.deleteById(idComment);
        }
    }

    @Override
    public void addGet(Model model) {
        model.addAttribute("statusList", statusRepository.findAll());
        model.addAttribute("userList", userRepository.findAll());
        model.addAttribute("groupList", groupUserRepository.findAll());
    }


    @Override
    public void changeStatus(int id){
        try{
            Status status = statusRepository.findById(id).orElse(null);
            int idCurrent = (int) session.getAttribute("idTaskI");
            Task task = taskRepository.findById(idCurrent).orElse(null);
            task.setStatus(status);
            taskRepository.save(task);
        }catch (NullPointerException e){
        }
    }
}
