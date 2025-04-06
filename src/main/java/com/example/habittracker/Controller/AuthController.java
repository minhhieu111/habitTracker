package com.example.habittracker.Controller;

import com.example.habittracker.DTO.Register;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.AuthService;
import com.example.habittracker.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new Register());
        return "auth/register";
    }
    @PostMapping("/register")
    public String register(Model model, @Valid @ModelAttribute("user")Register user
                            , BindingResult bindingResult
                            , RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        boolean existsEmail = authService.existsByEmail(user.getUsername());
        if (existsEmail) {
            bindingResult.rejectValue("email", "error.register", "Email đã tồn tại!");
            return "auth/register";
        }
        boolean existsUsername = authService.existsByUsername(user.getUsername());
        if (existsUsername) {
            bindingResult.rejectValue("userName", "error.register", "User Name đã tồn tại!");
            return "auth/register";
        }

        this.authService.registerUser(user);
        return "redirect:/login";
    }
}
