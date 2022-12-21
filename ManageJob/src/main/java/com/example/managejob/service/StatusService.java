package com.example.managejob.service;

import com.example.managejob.dto.StatusDTO;
import com.example.managejob.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;

public interface StatusService {

    void updateStatus(StatusDTO statusDTO, Model model);

    void findByName(String name, Model model, Integer page);

    void getStatusById(int id, Model model);

    String createStatus(StatusDTO statusDTO, Model model);

    void deleteStatus(int id);

    void getAllStatus(Integer page, Model model);
    Long count();

    //convert list to page
    Page toPage(List<StatusDTO> list, Pageable pageable);
}
