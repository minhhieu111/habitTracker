package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.RewardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("rewards")
public class RewardController {
    private final RewardService rewardService;
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;

    public RewardController(RewardService rewardService, JwtUtil jwtUtil, TokenUtil tokenUtil) {
        this.rewardService = rewardService;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping("/save")
    public String createReward(HttpServletRequest request, Model model, @Valid @ModelAttribute("newReward") Reward reward, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "redirect:/home";
        }
        try{
            String token = tokenUtil.getTokenFromCookies(request);
            String userName = jwtUtil.getUserNameFromToken(token);
            this.rewardService.save(reward, userName);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }

        return "redirect:/home";
    }
}
