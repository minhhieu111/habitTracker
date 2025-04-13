package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.RewardDTO;
import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Service.RewardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String createReward(HttpServletRequest request, Model model,@ModelAttribute("newReward") Reward reward, RedirectAttributes redirectAttributes) {
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

    @GetMapping("/{rewardId}")
    @ResponseBody
    public RewardDTO editReward(@PathVariable Long rewardId, Model model) {
        Reward reward = this.rewardService.getRewardById(rewardId);
        return new RewardDTO(reward.getRewardId(), reward.getTitle(), reward.getDescription(), reward.getCoinCost());
    }

    @PostMapping("/update")
    public String updateReward(Model model, @ModelAttribute("updateReward") Reward reward, RedirectAttributes redirectAttributes) {
        try{
            this.rewardService.updateReward(reward);
            redirectAttributes.addFlashAttribute("success","Cập nhật thành công");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }
        return "redirect:/home";
    }
}
