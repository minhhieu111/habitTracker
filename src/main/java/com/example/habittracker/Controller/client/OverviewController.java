package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.DiaryDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.DTO.TodoDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Service.*;
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
    private final TodoService todoService;
    private final DiaryService diaryService;

    public OverviewController(JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService, HabitService habitService, DailyService dailyService, ChallengeService challengeService, TodoService todoService, DiaryService diaryService) {
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.challengeService = challengeService;
        this.todoService = todoService;
        this.diaryService = diaryService;
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

        // Dailies
        model.addAttribute("newDaily",new DailyDTO());
        List<UserDaily> userdaily = this.dailyService.getUserDaily(user);
        model.addAttribute("userDailies",userdaily);

        //Todos
        model.addAttribute("newTodo", new TodoDTO());
        List<Todo> activeTodos = this.todoService.getActiveTodos(user);
        model.addAttribute("activeTodos", activeTodos);

        //Diary
        model.addAttribute("newDiary", new DiaryDTO());
        List<Diary> diaries = diaryService.getDiariesByUser(user);
        model.addAttribute("diaries", diaries);


        return "client/overview";
    }
}
