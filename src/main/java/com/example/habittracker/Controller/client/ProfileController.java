package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.UserDTO;
import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.DiaryService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final ChallengeService challengeService;
    private final DiaryService diaryService;

    public ProfileController(UserService userService, TokenUtil tokenUtil, JwtUtil jwtUtil, ChallengeService challengeService, DiaryService diaryService) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.challengeService = challengeService;
        this.diaryService = diaryService;
    }

    @GetMapping("")
    public String profile(HttpServletRequest request,Model model) {
        User user = getUserFromRequest(request);
        model.addAttribute("user", user);
        model.addAttribute("newUser", new UserDTO());

        List<UserChallenge> getParticipateChallenge = this.challengeService.getChallenges(user.getUserId());
        model.addAttribute("participatingChallenges",getParticipateChallenge);

        UserChallenge longestStreakChallenge = this.challengeService.getLongestStreakUserChallenges(user.getUserId());
        model.addAttribute("longestStreakChallenge",longestStreakChallenge);

        List<Diary> journalEntries = this.diaryService.getDiariesByUser(user);
        model.addAttribute("journalEntries",journalEntries);

        List<UserChallenge>completedChallenges = this.challengeService.getAllCompleteChallenge(user);
        model.addAttribute("completedChallenges",completedChallenges);

        Integer rankUser = this.userService.getUserRank(user.getUserId());
        model.addAttribute("rankUser",rankUser);

        long completedTask = this.userService.getTaskComplete(user);
        model.addAttribute("completedTask",completedTask);

        return "client/profile";
    }

    @PostMapping("/update")
    public String updateProfile(HttpServletRequest request, @ModelAttribute("newUser") UserDTO userDTO, @RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) {
        try{
            this.userService.updateUser(userDTO,image);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("failed", "Cập nhật thất bại"+e.getMessage());
            return "redirect:/profile";
        }
        return "redirect:/profile";
    }



    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}
