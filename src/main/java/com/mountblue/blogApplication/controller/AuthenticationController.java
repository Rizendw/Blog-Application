package com.mountblue.blogApplication.controller;

import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "/login";
    }

    @GetMapping("/signup")
    public String SignupPage() {
        System.err.println("Signup Page created");
        return "/signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        System.err.println("i'm in signup post mapping");
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "/signup";
        }
        if (password.length() < 2) {
            model.addAttribute("error", "Password must be between greater than 2 characters");
            return "/signup";
        }

        userService.registerUser(name, email, password);
        return "redirect:/login?success";
    }
}
