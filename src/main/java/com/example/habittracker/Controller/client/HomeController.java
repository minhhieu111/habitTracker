package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.CalendarDTO;
import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Service.CalendarService;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {
    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final ChallengeService challengeService;
    private final CalendarService calendarService;

    public HomeController(UserService userService, TokenUtil tokenUtil, JwtUtil jwtUtil, ChallengeService challengeService, CalendarService calendarService) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.challengeService = challengeService;
        this.calendarService = calendarService;
    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        User user = getUserFromRequest(request);

        model.addAttribute("user", user);

        //challenge
        List<UserChallenge> userChallenges  = this.challengeService.getChallenges(user.getUserId());
        model.addAttribute("userChallenges", userChallenges);

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
        model.addAttribute("updateReward", new Reward());
        return "client/home";
    }

    @GetMapping("/calendar/{date}")
    @ResponseBody
    public ResponseEntity<CalendarDTO> getCalendarData(@PathVariable String date, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        CalendarDTO calenderResponse = this.calendarService.calendarResponse(user, date);

        return ResponseEntity.ok(calenderResponse);
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}
