package com.example.managejob.controller;

import com.example.managejob.dto.UserDTO;
import com.example.managejob.model.User;
import com.example.managejob.service.MailService;
import com.example.managejob.service.SystemService;
import com.example.managejob.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("system")
public class SystemController {

    private final Logger logger = Logger.getLogger(SystemController.class);
    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Autowired
    SystemService systemService;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "system/login";
        }else{
            return "redirect:/";
        }
    }


    @GetMapping("/")
    public String home() {
        return "system/overView";
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
    public String register(@ModelAttribute UserDTO userDTO, Model model, @RequestParam("password1") String password,
                           @RequestParam(name="g-recaptcha-response") String captchaResponse) {
        systemService.register(userDTO, model, password, captchaResponse);
        return "system/register";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "system/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, @RequestParam("email") String email, HttpSession session) {
        int k = systemService.forgotPassword(model, email);
        if (k == 1) {
            model.addAttribute("emailInput", email);
            return "system/forgotPassword";
        } else {
            int code;
            code = (int) (Math.random() * 100000);
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

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errConfirmPass", "Password does not match");
            k = 1;
        }
        if (k == 1) {
            return "system/newPassword";
        } else {
            User user;
            user = userService.findByEmail((String) session.getAttribute("emailUserCurrent"));
            if (user == null) {
                user = userService.findById((int) session.getAttribute("idCurrentUser"));
            }
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userService.saveUser(user);
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
