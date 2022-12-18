package com.example.managejob.controller;

import com.example.managejob.model.*;
import com.example.managejob.repository.*;
import com.example.managejob.service.TaskExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("task")
public class TaskController {
    @Autowired
    TaskRepository tr;

    @Autowired
    GroupUserRepository groupUserRepository;

    @Autowired
    DocumentRepository dr;

    @Autowired
    UserRepository ur;

    @Autowired
    StatusRepository sr;

    @Autowired
    CommentRepository cr;

    @GetMapping("/view")
    public String view(Model model, @RequestParam(value = "id", required = false) Integer id, HttpSession session) {
        int ID = 0;
        if (id != null) {
            ID = id;
        } else {
            ID = (int) session.getAttribute("idTaskI");
        }
        Task task = tr.findById(ID).orElse(null);
        model.addAttribute("task", task);
        String idTask = String.valueOf(ID);
        session.setAttribute("idTask", idTask);
        session.setAttribute("idTaskI", ID);
        model.addAttribute("listC", cr.searchByTaskId(ID));
        model.addAttribute("listDocument", dr.searchByTaskId(ID));
        model.addAttribute("listStatus", sr.findAll());
        return "admin/task/view";
    }

    @PostMapping("/view")
    public String view(@RequestParam(value = "content", required = false) String content,
                       @RequestParam(value = "document", required = false) MultipartFile multipartFile,
                       Model model, HttpSession session, Principal principal) throws IOException {
        String id1 = (String) session.getAttribute("idTask");
        int id2 = Integer.parseInt((String) session.getAttribute("idTask"));
        Task task = tr.findById(id2).orElse(null);
        if (content != null) {
            Comment c = new Comment();
            c.setContent(content);
            c.setTask(task);
            c.setUser(ur.findByName(principal.getName()));
            c.setCreatedAt(new Date(System.currentTimeMillis()));
            cr.save(c);
        }
        if (multipartFile != null) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            Document d = new Document();
            d.setName(fileName);
            d.setContent(multipartFile.getBytes());
            d.setSize((int) multipartFile.getSize());
            d.setUploadTime(new Date());
            d.setTask(task);
            d.setUser(ur.findByName(principal.getName()));
            dr.save(d);
        }

        model.addAttribute("listDocument", dr.searchByTaskId(id2));
        model.addAttribute("listC", cr.searchByTaskId(id2));

        return "redirect:/task/view?id=" + id1;
    }

    @GetMapping("/deleteFile")
    public String deleteFile(@RequestParam(value = "idComment", required = false) Integer idComment,
                             @RequestParam(value = "idDocument", required = false) Integer idDocument,
                             HttpSession session) {
        String id1 = (String) session.getAttribute("idTask");
        if (idDocument != null) {
            dr.deleteById(idDocument);
        }

        System.err.println(idDocument);
        System.err.println(idComment);
        if (idComment != null) {
            cr.deleteById(idComment);
        }
        return "redirect:/task/view";
    }

    @GetMapping("/download")
    public void download(@RequestParam("id") int id, HttpServletResponse response) throws IOException {
        Document document = dr.findById(id).orElse(null);
        if (document != null) {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + document.getName();
            response.setHeader(headerKey, headerValue);

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(document.getContent());
            outputStream.close();
        }
    }

    //    @PostMapping("/upload")
//    public String document(@RequestParam("content") String content, Model model, HttpSession session, Principal principal) {
//        String id1 = (String) session.getAttribute("idTask");
//        int id2 = Integer.parseInt((String) session.getAttribute("idTask"));
//        Task task = tr.findById(id2).orElse(null);
//        Comment c = new Comment();
//        c.setContent(content);
//        c.setTask(task);
//        c.setUser(ur.findByName(principal.getName()));
//        c.setCreatedAt(new Date(System.currentTimeMillis()));
//        cr.save(c);
//        model.addAttribute("listC", cr.searchByTaskId(id2));
//
//        return "redirect:/task/view?id=" + id1;
//    }
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("statusList", sr.findAll());
        model.addAttribute("userList", ur.findAll());
        return "admin/task/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Task task, Principal principal, Model model, HttpSession session) throws IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]{4,150}$");
        Matcher m = pattern.matcher(task.getName());

        Pattern pattern2 = Pattern.compile("^[0-9]{0,150}$");
        Matcher m2 = pattern2.matcher(task.getSpentTime());

        if (!m2.find()) {
            model.addAttribute("errSpentTime", "enter number ");
            k = 1;
        }

        Pattern pattern1 = Pattern.compile("^[0-9]{1,150}$");
        Matcher m1 = pattern1.matcher(task.getEstimateHourse());

        if (!m1.find()) {
            model.addAttribute("errEstimateHourse", "enter number ");
            k = 1;
        }

        Task taskCheck = tr.findByName(task.getName());

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
            model.addAttribute("statusList", sr.findAll());
            model.addAttribute("userList", ur.findAll());
            return "admin/task/add";
        } else {
            task.setModifyBy(principal.getName());
            tr.save(task);
        }

        return "redirect:/task/list";
    }


    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model, HttpSession session) {
        session.setAttribute("idEditTask", id);
        Task task = tr.findById(id).orElse(null);
        model.addAttribute("task", task);
        model.addAttribute("statusList", sr.findAll());
        model.addAttribute("userList", ur.findAll());
        return "admin/task/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("task") Task task, Model model, HttpSession session, Principal principal) throws IllegalStateException, IOException {
        int k = 0;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]{4,150}$");
        Matcher m = pattern.matcher(task.getName());

        Pattern pattern2 = Pattern.compile("^[0-9]{0,150}$");
        Matcher m2 = pattern2.matcher(task.getSpentTime());

        if (!m2.find()) {
            model.addAttribute("errSpentTime", "enter number ");
            k = 1;
        }

        Pattern pattern1 = Pattern.compile("^[0-9]{0,150}$");
        Matcher m1 = pattern1.matcher(task.getEstimateHourse());

        if (!m1.find()) {
            model.addAttribute("errEstimateHourse", "enter number ");
            k = 1;
        }

        Task taskCheck = tr.findByName(task.getName());


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
            model.addAttribute("spentTime", task.getSpentTime());
            model.addAttribute("estimateHourse", task.getEstimateHourse());
            model.addAttribute("startDate", task.getStartDate());
            model.addAttribute("endDate", task.getEndDate());
            model.addAttribute("statusList", sr.findAll());
            model.addAttribute("userList", ur.findAll());
            return "admin/task/add";
        } else {
            Task current = tr.findById((int) session.getAttribute("idEditTask")).orElse(null);

            if (current != null) {
                // lay du lieu can update tu edit qua current, de tranh mat du lieu cu
                current.setId((int) session.getAttribute("idEditTask"));
                current.setName(task.getName());
                current.setStatus(task.getStatus());
                current.setEstimateHourse(task.getEstimateHourse());
                current.setSpentTime(task.getSpentTime());
                current.setStartDate(task.getStartDate());
                current.setEndDate(task.getEndDate());
                current.setModifyBy(principal.getName());
                current.setUser(task.getUser());
                tr.save(current);
            }
        }

        return "redirect:/task/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        tr.deleteById(id);
        return "redirect:/task/list";
    }


    @GetMapping("/myTask")
    public String myTask(Model model, @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "pageToDo", required = false) Integer pageToDo, HttpSession session) {
        int size = 5;
        //phan trang + get all list
        page = (page == null || page < 0) ? 0 : page;
        Pageable pageagle = PageRequest.of(page, size);
        int id = (int) session.getAttribute("idCurrentUser");
        Page<Task> pageUser = tr.findListUserById(id, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);

        // phan trang + get Todo List
        pageToDo = (pageToDo == null || pageToDo < 0) ? 0 : pageToDo;
        Pageable pageagleToDo = PageRequest.of(pageToDo, size);
        Page<Task> pageTodoList = tr.findListByStatusId("Todo List", id, pageagleToDo);
        model.addAttribute("listToDo", pageTodoList.getContent());
        model.addAttribute("pageToDo", pageToDo);
        model.addAttribute("totalPageToDo", pageTodoList.getTotalPages());

        Page<Task> pageReviewList = tr.findListByStatusId("Review", id, pageagle);
        model.addAttribute("listReview", pageReviewList.getContent());

        Page<Task> pageDoneList = tr.findListByStatusId("Done", id, pageagle);
        model.addAttribute("listDone", pageDoneList.getContent());

        Page<Task> pageInProgressList = tr.findListByStatusId("In Progress", id, pageagle);
        model.addAttribute("listInProgress", pageInProgressList.getContent());

        Page<Task> pageCancelList = tr.findListByStatusId("Cancel", id, pageagle);
        model.addAttribute("listCancel", pageCancelList.getContent());
        return "admin/task/taskUser";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.findAll(pageagle);
        model.addAttribute("listD", pageUser.getContent());

        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("listD", pageUser);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
        return "admin/task/list";
    }

    @GetMapping("/viewTaskUserGroup")
    public String viewTaskUserGroup(Model model,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "pageToDo", required = false) Integer pageToDo, HttpSession session) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        //phan trang + get all list
        page = (page == null || page < 0) ? 0 : page;
        Pageable pageagle = PageRequest.of(page, size);
        int id = ur.findByName(name).getId();
        Page<Task> pageUser = tr.findAllTaskByUserGroup(id, (int) session.getAttribute("idGroup"), pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);

        // phan trang + get Todo List
        pageToDo = (pageToDo == null || pageToDo < 0) ? 0 : pageToDo;
        Pageable pageagleToDo = PageRequest.of(pageToDo, size);
        Page<Task> pageTodoList = tr.findListByUserGroup("Todo List", id, (int) session.getAttribute("idGroup"), pageagleToDo);
        model.addAttribute("listToDo", pageTodoList.getContent());
        model.addAttribute("pageToDo", pageToDo);
        model.addAttribute("totalPageToDo", pageTodoList.getTotalPages());

        Page<Task> pageReviewList = tr.findListByUserGroup("Review", id, (int) session.getAttribute("idGroup"), pageagle);
        model.addAttribute("listReview", pageReviewList.getContent());

        Page<Task> pageDoneList = tr.findListByUserGroup("Done", id, (int) session.getAttribute("idGroup"), pageagle);
        model.addAttribute("listDone", pageDoneList.getContent());

        Page<Task> pageInProgressList = tr.findListByUserGroup("In Progress", id, (int) session.getAttribute("idGroup"), pageagle);
        model.addAttribute("listInProgress", pageInProgressList.getContent());

        return "admin/task/viewTaskUserGroup";
    }

    @GetMapping("/listByStatus")
    public String listByStatus(Model model, @RequestParam(value = "page", required = false) Integer page, @RequestParam("status1") String status1) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.findListByStatus(status1, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
        return "admin/task/list";
    }

    @GetMapping("/listByStatusId")
    public String listByStatusId(Model model, @RequestParam(value = "page", required = false) Integer page, @RequestParam("status1") String status1, HttpSession session) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 8;
        int id = (int) session.getAttribute("idCurrentUser");
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.findListByStatusId(status1, id, pageagle);
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPage", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
        return "admin/task/taskUser";
    }

    @GetMapping("/search")
    public String search(@RequestParam("name") String name, Model model, @RequestParam(value = "page", required = false) Integer page) {
        page = page == null || page < 0 ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        Page<Task> pageUser = tr.searchByName(name, pageagle);
        if (name.equalsIgnoreCase("")) {
            pageUser = tr.findAll(pageagle);
        }
        model.addAttribute("listD", pageUser.getContent());
        model.addAttribute("totalPages", pageUser.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("listD", pageUser);
        long count = pageUser.getContent().size();
        model.addAttribute("count", count);
        return "admin/task/list";
    }

    @GetMapping("/searchTaskUser")
    public String searchTaskUser(Model model,
                                 HttpSession session,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "group", required = false) String group,
                                 @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                 @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
                                    ) {
        page = page == null || page < 0 ? 0 : page;
        int size = 8;
        Pageable pageagle = PageRequest.of(page, size);
        int idCurrentUser = (int) session.getAttribute("idCurrentUser");
        Page<Task> pageTask = null;
        if (StringUtils.hasText(name) && startDate != null && endDate != null && StringUtils.hasText(group)) {
            pageTask = tr.searchByNameAndGroupAndDate(name, idCurrentUser, startDate, endDate,group , pageagle);
        } else if (StringUtils.hasText(name) && StringUtils.hasText(group)) {
            pageTask = tr.searchByNameAndGroup(name, idCurrentUser, group, pageagle);
        } else if (StringUtils.hasText(name)) {
            pageTask = tr.searchByName(name, idCurrentUser, pageagle);
        } else if(StringUtils.hasText(group)){
            pageTask = tr.searchByGroup(group, idCurrentUser, pageagle);
        } else if(startDate != null){
            pageTask = tr.searchByStartDate(startDate, idCurrentUser, pageagle);
        }else if(endDate != null){
            pageTask = tr.searchByEndDate(endDate, idCurrentUser, pageagle);
        }else if(startDate != null && endDate != null){
            pageTask = tr.searchByEndDateAndStartDate(startDate, endDate, idCurrentUser, pageagle);
        }
        else {
            pageTask = tr.findListUserById((int) session.getAttribute("idCurrentUser"), pageagle);
        }
        model.addAttribute("name", name);
        model.addAttribute("group", group);
        model.addAttribute("startDate", startDate);
        model.addAttribute("listD", pageTask.getContent());
        model.addAttribute("totalPages", pageTask.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", pageTask.getSize());
        return "admin/task/taskSearch";
    }

    @GetMapping("/exportExcel")
    public void exportData(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        String currentDateTime = dateFormat.format(new Date());
        String fileName = "tasks_" + currentDateTime + ".xlsx";
        String headerValue = "attachement; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        List<Task> listTasks = tr.findAll();

        TaskExcelExporter excelExporter = new TaskExcelExporter(listTasks);
        excelExporter.export(response);

    }
}
