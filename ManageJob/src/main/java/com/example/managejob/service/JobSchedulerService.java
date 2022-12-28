package com.example.managejob.service;

import com.example.managejob.model.Task;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Component
public class JobSchedulerService {

    @Autowired
    MailService mailService;
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    StatusRepository statusRepository;

    @Scheduled(cron = "0 30 9 * * *")
    @Transactional
    public void sayHello() {
        List<Task> list = taskRepository.checkEndDate(new Date());
        if (!list.isEmpty()) {
            for (Task task : list) {
                task.setCheck(1);
                taskRepository.save(task);
                String email = task.getUser().getEmail();
                mailService.sendEmailTask(email, "Hello", "Task hết hạn: " + task.getName());
            }
        }
    }
}
