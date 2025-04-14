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

    public OverviewController(JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
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
        List<Map<String, Object>> habits = new ArrayList<>();

        Map<String, Object> habit1 = new HashMap<>();
        habit1.put("id", 1);
        habit1.put("name", "Uống nước");
        habit1.put("description", "Uống nước mỗi 1 giờ");
        habit1.put("color", "yellow");
        habit1.put("count", 11);
        habits.add(habit1);

        Map<String, Object> habit2 = new HashMap<>();
        habit2.put("id", 2);
        habit2.put("name", "Đọc sách");
        habit2.put("description", "Đọc 30 phút");
        habit2.put("color", "green");
        habit2.put("count", 10);
        habits.add(habit2);

        Map<String, Object> habit3 = new HashMap<>();
        habit3.put("id", 3);
        habit3.put("name", "Dậy muộn");
        habit3.put("description", "Hôm nay muộn 1 giờ");
        habit3.put("color", "red");
        habit3.put("count", 12);
        habits.add(habit3);

        model.addAttribute("habits", habits);

        // Dailies
        List<Map<String, Object>> dailies = new ArrayList<>();

        Map<String, Object> daily1 = new HashMap<>();
        daily1.put("id", 1);
        daily1.put("name", "Ngủ trước 10 giờ");
        daily1.put("color", "yellow");
        daily1.put("count", 11);
        daily1.put("completed", false);
        dailies.add(daily1);

        Map<String, Object> daily2 = new HashMap<>();
        daily2.put("id", 2);
        daily2.put("name", "Tập thể dục");
        daily2.put("color", "green");
        daily2.put("count", 20);
        daily2.put("completed", false);
        dailies.add(daily2);

        Map<String, Object> daily3 = new HashMap<>();
        daily3.put("id", 3);
        daily3.put("name", "Học Tiếng Anh");
        daily3.put("color", "red");
        daily3.put("count", 17);
        daily3.put("completed", true);
        dailies.add(daily3);

        model.addAttribute("dailies", dailies);

        // Todos
        List<Map<String, Object>> todos = new ArrayList<>();

        Map<String, Object> todo1 = new HashMap<>();
        todo1.put("id", 1);
        todo1.put("name", "Làm việc nhà");
        todo1.put("color", "yellow");
        todo1.put("completed", false);
        todos.add(todo1);

        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("id", 2);
        todo2.put("name", "Dọn phòng");
        todo2.put("color", "yellow");
        todo2.put("completed", true);
        todos.add(todo2);

        Map<String, Object> todo3 = new HashMap<>();
        todo3.put("id", 3);
        todo3.put("name", "Quét, lau nhà");
        todo3.put("color", "green");
        todo3.put("completed", false);
        todos.add(todo3);

        Map<String, Object> todo4 = new HashMap<>();
        todo4.put("id", 4);
        todo4.put("name", "Giặt quần áo");
        todo4.put("color", "red");
        todo4.put("completed", false);
        todos.add(todo4);

        Map<String, Object> todo5 = new HashMap<>();
        todo5.put("id", 5);
        todo5.put("name", "Đi chợ");
        todo5.put("color", "yellow");
        todo5.put("completed", false);
        todos.add(todo5);

        model.addAttribute("todos", todos);

        return "client/overview";
    }
}
