package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    public ProfileController(UserService userService, TokenUtil tokenUtil, JwtUtil jwtUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("")
    public String profile(HttpServletRequest request,Model model) {
        User user = getUserFromRequest(request);
        model.addAttribute("user", user);
        // User profile data
        model.addAttribute("username", "user");
        model.addAttribute("title", "Người mới bắt đầu");
        model.addAttribute("email", "User@gmail.com");
        model.addAttribute("gold", 100);
        model.addAttribute("avatar", "😀");

        // User statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("participatingChallenges", 12);
        stats.put("longestStreak", 20);
        stats.put("longestStreakChallenge", "thay đổi");
        stats.put("journalEntries", 12);
        stats.put("leaderboardRank", 12);
        stats.put("completedTasks", 120);
        stats.put("completedChallenges", 5);

        model.addAttribute("stats", stats);

        // Additional profile data
        model.addAttribute("joinDate", "Tháng 3, 2024");
        model.addAttribute("totalDaysActive", 45);
        model.addAttribute("favoriteCategory", "Thể thao");
        return "client/profile";
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}
