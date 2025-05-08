package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.TodoDTO;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.TodoService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todos")
public class TodoController {
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final TodoService todoService;
    private final UserService userService;

    public TodoController(TokenUtil tokenUtil, JwtUtil jwtUtil, TodoService todoService, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.todoService = todoService;
        this.userService = userService;
    }

    @PostMapping("/save")
    public String saveTodo(@ModelAttribute("newTodo") TodoDTO todoDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String token = this.tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        try{
            User user = this.userService.getUser(username);
            this.todoService.saveTodo(todoDTO, user);
            redirectAttributes.addFlashAttribute("success", "Thêm mới thành công!");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("fail", e.getMessage());
            return "redirect:/overview";
        }

        return "redirect:/overview";
    }
}
