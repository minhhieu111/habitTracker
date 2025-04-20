package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Service.HabitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habit")
public class HabitController {
    private final HabitService habitService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    public HabitController(HabitService habitService, TokenUtil tokenUtil, JwtUtil jwtUtil) {
        this.habitService = habitService;
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public String save(HttpServletRequest request, @ModelAttribute("newHabit") HabitDTO habitDTO, RedirectAttributes redirectAttributes) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        try{
            this.habitService.save(habitDTO,userName);
            redirectAttributes.addFlashAttribute("success", "Tạo thói quen thành công!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("failed", "Tạo thói quen thất bại!");
            return "redirect:/overview";
        }
        return "redirect:/overview";
    }
}
