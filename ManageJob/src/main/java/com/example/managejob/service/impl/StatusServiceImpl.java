package com.example.managejob.service.impl;

import com.example.managejob.dto.StatusDTO;
import com.example.managejob.model.Status;
import com.example.managejob.repository.StatusRepository;
import com.example.managejob.service.StatusService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    HttpSession session;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    StatusRepository statusRepository;

    @Override
    public String createStatus(StatusDTO statusDTO, Model model) {
        Status status = modelMapper.map(statusDTO, Status.class);
        int k = 0;
        Pattern statusP = Pattern.compile("^^[0-9a-zA-Z]{1,}$");
        Matcher m2 = statusP.matcher(status.getStatus1());

        Status statusCheck = statusRepository.findByStatus1(status.getStatus1());

        if (statusCheck != null) {
            model.addAttribute("errStatus", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errStatus", "Not empty");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("status1", status.getStatus1());
            return "admin/status/add";
        } else {
            model.addAttribute("success", "Insert successfully");
            statusRepository.save(status);
        }
        return "redirect:/status/list";
    }
    @Override
    public void updateStatus(StatusDTO statusDTO, Model model) {
        Status status = modelMapper.map(statusDTO, Status.class);
        status.setId((int) session.getAttribute("idStatus"));
        int k = 0;
        Pattern statusP = Pattern.compile("^^[0-9a-zA-Z]{1,}$");
        Matcher m2 = statusP.matcher(status.getStatus1());
        Status statusCheck = statusRepository.findByStatus1(status.getStatus1());
        if (statusCheck != null && statusCheck.getId() != (int) session.getAttribute("idStatus")) {
            model.addAttribute("errStatus", "Duplicate status");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("errStatus", "Not empty");
            k = 1;
        }
        if (k == 1) {
            model.addAttribute("status1", statusDTO.getStatus1());
//            return "admin/status/edit";
        } else {
            model.addAttribute("success", "Edit successfully");
            statusRepository.save(status);
//            return "redirect:/status/list";
        }
    }

    @Override
    public void getAllStatus(Integer page, Model model) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        List<StatusDTO> listStatus = statusRepository.findAll().stream().map(status
                -> modelMapper.map(status, StatusDTO.class)).collect(Collectors.toList());
        Page<StatusDTO> pageStatus = toPage(listStatus, pageagle);
        model.addAttribute("listD", pageStatus);
        model.addAttribute("totalPage", pageStatus.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", statusRepository.count());
    }

    @Override
    public void findByName(String name, Model model, Integer page) {
        page = (page == null || page < 0) ? 0 : page;
        int size = 5;
        Pageable pageagle = PageRequest.of(page, size);
        List<StatusDTO> listStatus = statusRepository.findByName(name).stream().map(status
                -> modelMapper.map(status, StatusDTO.class)).collect(Collectors.toList());
        Page<StatusDTO> pageStatus = toPage(listStatus, pageagle);
        model.addAttribute("listD", pageStatus);
        model.addAttribute("totalPage", pageStatus.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("count", statusRepository.count());
    }

    @Override
    public void getStatusById(int id, Model model) {
        Status status = statusRepository.findById(id).orElse(null);
        // convert entity to dto
        StatusDTO statusDTO = modelMapper.map(status, StatusDTO.class);
        model.addAttribute("statusDTO", statusDTO);
        session.setAttribute("idStatus", id);
    }

    @Override
    public void deleteStatus(int id) {
        statusRepository.deleteById(id);
    }

    @Override
    public Long count() {
        return statusRepository.count();
    }

    @Override
    public Page toPage(List<StatusDTO> list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size()) ? list.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }
}
