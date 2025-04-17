package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.DailyRepository;
import com.example.habittracker.Repository.HabitRepository;
import com.example.habittracker.Service.DailyService;
import com.example.habittracker.Service.HabitService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/overview")
public class OverviewController {
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final UserService userService;
    private final HabitService habitService;
    private final DailyService dailyService;

    public OverviewController(JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService, HabitService habitService, DailyService dailyService) {
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
    }

    @GetMapping("")
    public String overview(HttpServletRequest request,Model model) {
        // User data
        String token = this.tokenUtil.getTokenFromCookies(request);
        String userName = this.jwtUtil.getUserNameFromToken(token);
        User user = this.userService.getUser(userName);



        model.addAttribute("user", user);
        model.addAttribute("username", "Người mới bắt đầu");
        model.addAttribute("level", 2);
        model.addAttribute("experience", 120);
        model.addAttribute("maxExperience", 200);
        model.addAttribute("gold", 100);

        // Challenge data
        model.addAttribute("challengeName", "Lối Sống");
        model.addAttribute("challengeDay", 24);
        model.addAttribute("challengeTotalDays", 30);
        model.addAttribute("streak", 5);

        // Progress data
        model.addAttribute("streakCount", 5);
        model.addAttribute("maxStreak", 15);
        model.addAttribute("weeklyProgress", 5);
        model.addAttribute("weeklyTotal", 7);

        // Habits
        List<Habit> userhabit = this.habitService.getUserHabits(user);
        model.addAttribute("userHabits",userhabit);


        // Dailies
        List<UserDaily> userdaily = this.dailyService.getUserDaily(user);
        model.addAttribute("userDailies",userdaily);
        return "client/overview";
    }
}
