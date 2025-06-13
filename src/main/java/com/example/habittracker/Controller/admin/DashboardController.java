package com.example.habittracker.Controller.admin;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.Achievement;
import com.example.habittracker.Domain.Challenge;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Service.AchievementService;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class DashboardController {
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ChallengeService challengeService;
    private final AchievementService achievementService;

    public DashboardController(TokenUtil tokenUtil, JwtUtil jwtUtil, UserService userService, ChallengeService challengeService, AchievementService achievementService) {
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.challengeService = challengeService;
        this.achievementService = achievementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        User userAdmin = getUserAdmin(request);
        model.addAttribute("userAdmin", userAdmin);

        List<User> allUsers = this.userService.getAllUsers();
        model.addAttribute("allUsers", allUsers);

        List<User> newUsers = allUsers.stream().filter(u -> u.getCreateAt().toLocalDate().equals(LocalDate.now())).collect(Collectors.toList());
        model.addAttribute("newUsers", newUsers);

        List<Challenge> pendingChallenge = this.challengeService.getAllPendingChallenge();
        model.addAttribute("pendingChallenge", pendingChallenge);

        List<Achievement> achievements = this.achievementService.getAllAchievement();
        model.addAttribute("achievements", achievements);
        return "admin/dashboard";
    }
    public User getUserAdmin(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String email = jwtUtil.getEmailFromToken(token);

        return this.userService.getUser(email);
    }
}
