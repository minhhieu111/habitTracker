package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.Reward;
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

        model.addAttribute("newReward", new Reward());
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
