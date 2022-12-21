package com.example.managejob.controller;

import com.example.managejob.model.User;
import com.example.managejob.repository.UserRepository;
import com.example.managejob.service.MailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("system")
public class SystemController {

    private  static Logger logger = Logger.getLogger(SystemController.class);
    @Autowired
    MailService mailService;

    @Autowired
    UserRepository ur;

    @GetMapping("/error403")
    public String error403() {
        logger.error("error 403");
        return "error403";
    }

    @GetMapping("/help")
    public String help() {
        return "system/help";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "e", required = false) String error) {
        model.addAttribute("message", "");
        if (error != null) {
            model.addAttribute("e", error);
            logger.error("login fail");
        }
        return "system/login";
    }



    @GetMapping("/")
    public String home() {
        return "headerU.html";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("errName", "");
        model.addAttribute("errEmail", "");
        model.addAttribute("errPass", "");
        model.addAttribute("errPass1", "");
        return "system/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model, @RequestParam("password1") String Password1, HttpSession session) {
        int k = 0;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{6,}$");
        Matcher m = pattern.matcher(user.getName());

        Pattern pattern1 = Pattern.compile("^[0-9a-zA-Z]{6,}$");
        Matcher m1 = pattern1.matcher(user.getPassword());

        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(user.getEmail());

        User userCheck = ur.findByName(user.getName());

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

        if (!user.getPassword().equals(Password1)) {
            model.addAttribute("errPass1", "Mật khẩu không khớp");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("name", user.getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("pass", user.getPassword());
            model.addAttribute("pass1", Password1);
            return "system/register";
        } else {
//            user.setRole("ROLE_MEMBER");
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setCreatedAt(new Date(System.currentTimeMillis()));
            session.setAttribute("user", user);

            int code = 0;
            code = (int) (Math.random() * 100000);
            System.err.println(code);
            System.err.println(user.getEmail());

            mailService.sendEmail(user.getEmail(), "a", code + "");
            session.setAttribute("code", code);
            return "redirect:/system/checkCode";
            //ur.save(user);
        }
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "system/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, @RequestParam("email") String email, HttpSession session) {
        int k = 0;
        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher m2 = pattern2.matcher(email);

        User user = ur.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "Email is not registered");
            k = 1;
        }
        if (!m2.find()) {
            model.addAttribute("message", "Wrong format (EX: A@gmail.com)");
            k = 1;
        }

        if (k == 1) {
            model.addAttribute("emailInput", email);
            return "system/forgotPassword";
        } else{
            int code = 0;
            code =(int) (Math.random() * 100000);
            session.setAttribute("code", code);
            session.setAttribute("emailUserCurrent", email);
            model.addAttribute("message", "email is in progress");

            mailService.sendEmail("manh7135@gmai.com", "a", code + "");
        }


        return "redirect:/system/checkCode";
    }

    @GetMapping("/newPassword")
    public String newPassword() {
        return "system/newPassword";
    }

    @PostMapping("/newPassword")
    public String newPassword(Model model, HttpSession session, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        int k = 0;

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{4,}$");
        Matcher m1 = pattern.matcher(confirmPassword);
        Matcher m2 = pattern.matcher(newPassword);

        if (!m2.find()) {
            model.addAttribute("errNewPass", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if (!m1.find()) {
            model.addAttribute("errConfirmPass", "Enter length greater than 4 characters and lesser 150 characters");
            k = 1;
        }

        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("errConfirmPass", "Password does not match");
            k = 1;
        }
        if(k == 1){
            return "system/newPassword";
        }else {
            User user = ur.findByEmail((String)session.getAttribute("emailUserCurrent"));
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            ur.save(user);
            return "redirect:/system/login";
        }

    }

    @GetMapping("/checkCode")
    public String checkCode(Model model) {
        model.addAttribute("message", "");
        return "system/checkCode";
    }

    @PostMapping("/checkCode")
    public String checkCode(@RequestParam("codeG") int codeG, HttpSession session, Model model) {
        if (codeG == (int) session.getAttribute("code")) {
            return "system/newPassword";
        }
        model.addAttribute("message", "Mã code không đúng!");
        return "system/checkCode";
    }
}
