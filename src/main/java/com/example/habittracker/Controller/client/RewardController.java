package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.RewardDTO;
import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.RewardService;
import com.example.habittracker.Service.UserService;
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
    private final UserService userService;

    public RewardController(RewardService rewardService, JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService) {
        this.rewardService = rewardService;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @PostMapping("/save")
    public String createReward(HttpServletRequest request, Model model,@ModelAttribute("newReward") Reward reward, RedirectAttributes redirectAttributes) {
        try{
            String token = tokenUtil.getTokenFromCookies(request);
            String userName = jwtUtil.getUserNameFromToken(token);
            if(userName == null) {
                redirectAttributes.addFlashAttribute("expired", "Phiên đăng nhập hết hạn xin mời đăng nhập lại!");
                return "redirect:/login";
            }
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
    public String updateReward(HttpServletRequest request,Model model, @ModelAttribute("updateReward") Reward reward, RedirectAttributes redirectAttributes) {
        try{
            String token = tokenUtil.getTokenFromCookies(request);
            String userName = jwtUtil.getUserNameFromToken(token);
            User user = this.userService.getUser(userName);
            if (user==null){
                redirectAttributes.addFlashAttribute("expired", "Phiên đăng nhập hết hạn xin mời đăng nhập lại!");
                return "redirect:/login";
            }
            this.rewardService.updateReward(reward);
            redirectAttributes.addFlashAttribute("success","Cập nhật thành công");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }
        return "redirect:/home";
    }

    @GetMapping("delete/{id}")
    public String deleteReward(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try{
            String token = tokenUtil.getTokenFromCookies(request);
            String userName = jwtUtil.getUserNameFromToken(token);
            User user = this.userService.getUser(userName);
            if (user==null){
                redirectAttributes.addFlashAttribute("expired", "Phiên đăng nhập hết hạn xin mời đăng nhập lại!");
                return "redirect:/login";
            }
            this.rewardService.deleteReward(id);
            redirectAttributes.addFlashAttribute("sucess","Xóa thành công!");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }
        return "redirect:/home";
    }

    @GetMapping("exchange/{rewardId}")
    public String exchangeReward(HttpServletRequest request, @PathVariable Long rewardId, Model model, RedirectAttributes redirectAttributes) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        User user = this.userService.getUser(userName);
        Reward reward = this.rewardService.getRewardById(rewardId);
        try{
            Long exchangeCost = this.rewardService.exchangeReward(user,reward);
            redirectAttributes.addFlashAttribute("exchange", exchangeCost);
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/home";
        }
        return "redirect:/home";
    }
}
