package com.example.managejob.service;

import com.example.managejob.dto.UserDTO;
import org.springframework.ui.Model;

public interface SystemService {
    void register(UserDTO userDTO, Model model,String password, String capchaResponse);

    int forgotPassword(Model model, String email);
}
