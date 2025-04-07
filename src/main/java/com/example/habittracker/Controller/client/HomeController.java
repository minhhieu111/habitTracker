package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("")
public class HomeController {
    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    public HomeController(UserService userService, TokenUtil tokenUtil, JwtUtil jwtUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;

        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        User user = this.userService.getUser(userName);

        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("today", today);
        model.addAttribute("months", new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

        // User data
        model.addAttribute("username", "Người mới bắt đầu");
        model.addAttribute("level", 10);
        model.addAttribute("experience", 120);
        model.addAttribute("maxExperience", 200);
        model.addAttribute("challengeDay", 24);

        // Habits data
        Map<String, Object> sportsHabit = new HashMap<>();
        sportsHabit.put("name", "Chơi Thể Thao");
        sportsHabit.put("progress", 50);
        sportsHabit.put("tasksCount", 4);

        Map<String, Object> programmingHabit = new HashMap<>();
        programmingHabit.put("name", "Học Lập Trình");
        programmingHabit.put("progress", 20);
        programmingHabit.put("tasksCount", 16);

        Map<String, Object> lifestyleHabit = new HashMap<>();
        lifestyleHabit.put("name", "Lối Sống");
        lifestyleHabit.put("progress", 63);
        lifestyleHabit.put("tasksCount", 6);

        Map<String, Object> financeHabit = new HashMap<>();
        financeHabit.put("name", "Tài chính");
        financeHabit.put("progress", 0);
        financeHabit.put("tasksCount", 5);

        model.addAttribute("habits", new Object[]{sportsHabit, programmingHabit, lifestyleHabit, financeHabit});

        // Rewards data
        Map<String, Object> watchMovieReward = new HashMap<>();
        watchMovieReward.put("name", "Xem Phim");
        watchMovieReward.put("points", 10);

        Map<String, Object> eatReward = new HashMap<>();
        eatReward.put("name", "Ăn Vặt");
        eatReward.put("points", 20);

        Map<String, Object> playReward = new HashMap<>();
        playReward.put("name", "Đi ngủ");
        playReward.put("points", 40);



        model.addAttribute("rewards", new Object[]{watchMovieReward, eatReward, playReward});

        return "client/home";
    }

    @GetMapping("/api/calendar/{date}")
    @ResponseBody
    public String getCalendarData(@PathVariable String date) {
        // Giả lập dữ liệu cho API
        try {
            LocalDate selectedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return "Thông tin cho ngày " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    ": Bạn có 10 thói quen cần hoàn thành.";
        } catch (Exception e) {
            return "Lỗi: Định dạng ngày không hợp lệ.";
        }
    }
}
