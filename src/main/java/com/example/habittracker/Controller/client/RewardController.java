package com.example.habittracker.Controller.client;

import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Service.RewardService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("rewards")
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping("/save")
    public String createReward(Model model, @Valid @ModelAttribute("newReward") Reward reward, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "redirect:/home";
        }
        try{
            this.rewardService.save(reward);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }

        return "redirect:/home";
    }
}
