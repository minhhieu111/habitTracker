package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.DailyService;
import com.example.habittracker.Service.HabitService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/overview")
public class OverviewController {
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final UserService userService;
    private final HabitService habitService;
    private final DailyService dailyService;
    private final ChallengeService challengeService;

    public OverviewController(JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService, HabitService habitService, DailyService dailyService, ChallengeService challengeService) {
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.challengeService = challengeService;
    }

    @GetMapping("")
    public String overview(HttpServletRequest request,Model model) {
        // User data
        String token = this.tokenUtil.getTokenFromCookies(request);
        String userName = this.jwtUtil.getUserNameFromToken(token);
        User user = this.userService.getUser(userName);



        model.addAttribute("user", user);

        // Challenge data
        List<UserChallenge> userChallenges  = this.challengeService.getChallenges(user.getUserId());
        model.addAttribute("userChallenges", userChallenges);

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
        model.addAttribute("newHabit",new HabitDTO());
        List<UserHabit> userhabit = this.habitService.getUserHabits(user);
        model.addAttribute("userHabits",userhabit);
        model.addAttribute("updateHabit",new HabitDTO());

        // Dailies
        model.addAttribute("newDaily",new DailyDTO());
        List<UserDaily> userdaily = this.dailyService.getUserDaily(user);
        model.addAttribute("userDailies",userdaily);
        model.addAttribute("updateDaily",new DailyDTO());

        return "client/overview";
    }
}
