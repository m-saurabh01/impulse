package com.wipro.iaf.email.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wipro.iaf.email.user.service.UserService;

@Controller
public class AuthController {
	
	@Autowired
	private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String doSignup(@RequestParam String email,
                           @RequestParam String password) {

        userService.register(email, password);
        return "redirect:/login";
    }
}
