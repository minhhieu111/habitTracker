package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.Service.DailyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dailies")
public class DailyController {
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final DailyService dailyService;

    public DailyController(JwtUtil jwtUtil, TokenUtil tokenUtil, DailyService dailyService) {
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.dailyService = dailyService;
    }

    @PostMapping("/create")
    public String createDaily(HttpServletRequest request, @ModelAttribute("newDaily")DailyDTO dailyDTO, RedirectAttributes redirectAttributes) {
        String token = this.tokenUtil.getTokenFromCookies(request);
        String username = this.jwtUtil.getUserNameFromToken(token);
        try{
            this.dailyService.createDaily(dailyDTO, username);
            redirectAttributes.addFlashAttribute("success", "Tạo thói quen hằng ngày thành công!");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/overview";
        }
        return "redirect:/overview";
    }
}
