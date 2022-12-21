package com.example.managejob.controller;

import com.example.managejob.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@RequestMapping("/")
@Controller
public class HomeController {
    @Autowired
    UserRepository ur;

    @GetMapping("/")
    public String home(){
        return "/system/overView";
    }

    @ModelAttribute
    public void setAll(Principal principal, HttpSession session) {
        if (principal == null) {
            session.setAttribute("currentUser", "Account");
            session.setAttribute("imgAvatar", "/user/download?filename=tda.jpg");
        } else {
            String pathAvatar = "/user/download?filename=";
            String avatar = ur.findByName(principal.getName()).getAvatar();
            session.setAttribute("imgAvatar", pathAvatar + avatar);
            session.setAttribute("currentUser", principal.getName());
            session.setAttribute("idCurrentUser", ur.findByName(principal.getName()).getId());

        }
    }
}
