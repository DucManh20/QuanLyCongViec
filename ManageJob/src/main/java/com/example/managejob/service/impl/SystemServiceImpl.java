package com.example.managejob.service.impl;

import com.example.managejob.dto.UserDTO;
import com.example.managejob.model.ReCaptchaResponse;
import com.example.managejob.model.User;
import com.example.managejob.repository.RoleRepository;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.MailService;
import com.example.managejob.service.SystemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SystemServiceImpl implements SystemService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    HttpSession session;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void register(UserDTO userDTO, Model model, String password, String capchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=6Lf09p4jAAAAAAdMkLRhxDA_nFjVQidOAwDkTucg&response=" + capchaResponse;
        ReCaptchaResponse reCaptchaResponse = restTemplate.exchange(url + params, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();
        User user = modelMapper.map(userDTO, User.class);
        if (reCaptchaResponse.isSuccess()) {

            int k = 0;
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9 ]{6,}$");
            Matcher m = pattern.matcher(user.getName());

            Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{4,}$");
            Matcher m1 = pattern1.matcher(user.getPassword());

            Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
            Matcher m2 = pattern2.matcher(user.getEmail());

            User userCheck = userRepository.findByName(user.getName());

            if (userCheck != null) {
                model.addAttribute("errName", "Tên người dùng đã tồn tại");
                k = 1;
            }
            if (!m.find()) {
                model.addAttribute("errName", "Nhập độ dài lớn hơn 6 ký tự");
                k = 1;
            }

            if (!m1.find()) {
                model.addAttribute("errPass", "Nhập độ dài lớn hơn 6 ký tự");
                k = 1;
            }

            if (!m2.find()) {
                model.addAttribute("errEmail", "Nhập sai định dạng(VD: A@gmail.com)");
                k = 1;
            }

            if (!user.getPassword().equals(password)) {
                model.addAttribute("errPass1", "Mật khẩu không khớp");
                k = 1;
            }

            if (k == 1) {
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
            } else {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                user.setCreatedAt(new Date(System.currentTimeMillis()));
                user.setRoleUser(roleRepository.findByRole("ROLE_MEMBER"));
                user.setModifyBy(user.getName());
                user.setAvatar("20190913_224545.jpg");
                userRepository.save(user);
                model.addAttribute("success", "Success");
            }
        } else {
            model.addAttribute("message", "Please verify captcha");
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
        }

    }

    @Override
    public int forgotPassword(Model model, String email) {
        int k = 0;
        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "Email is not registered");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("message", "Wrong format (EX: A@gmail.com)");
            k = 1;
        }
        return k;
    }
}
