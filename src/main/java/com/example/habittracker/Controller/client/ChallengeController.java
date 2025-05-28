package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Service.ChallengeService;
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
@RequestMapping("/challenges")
public class ChallengeController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final ChallengeService challengeService;

    public ChallengeController(UserService userService, JwtUtil jwtUtil, TokenUtil tokenUtil, ChallengeService challengeService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.challengeService = challengeService;
    }

    @GetMapping
    public String challenges(HttpServletRequest request, Model model) {

        User user = getUserFromRequest(request);
        // User data
        model.addAttribute("user", user);

        List<UserChallenge> userChallenges  = this.challengeService.getChallenges(user.getUserId());
        model.addAttribute("challenges", userChallenges);


        return "client/challenge";
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}

