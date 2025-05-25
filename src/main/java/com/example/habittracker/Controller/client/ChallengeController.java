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
        model.addAttribute("userChallenges", userChallenges);

        // Challenges data
        List<Map<String, Object>> challenges = new ArrayList<>();

        Map<String, Object> challenge1 = new HashMap<>();
        challenge1.put("id", 1);
        challenge1.put("name", "Chơi Thể Thao");
        challenge1.put("description", "Mô tả: Duy trì chơi thể thao thay đổi hình thể");
        challenge1.put("currentDays", 20);
        challenge1.put("totalDays", 50);
        challenge1.put("streak", 16);
        challenge1.put("color", "blue");
        challenges.add(challenge1);

        Map<String, Object> challenge2 = new HashMap<>();
        challenge2.put("id", 2);
        challenge2.put("name", "Học Lập Trình");
        challenge2.put("description", "Mô tả: Học nâng cao kỹ năng thông tin trong công việc");
        challenge2.put("currentDays", 30);
        challenge2.put("totalDays", 60);
        challenge2.put("streak", 30);
        challenge2.put("color", "green");
        challenges.add(challenge2);

        Map<String, Object> challenge3 = new HashMap<>();
        challenge3.put("id", 3);
        challenge3.put("name", "Lối Sống");
        challenge3.put("description", "Mô tả: Thay đổi lối thói quen trở nên lành mạnh");
        challenge3.put("currentDays", 24);
        challenge3.put("totalDays", 30);
        challenge3.put("streak", 5);
        challenge3.put("color", "yellow");
        challenges.add(challenge3);

        Map<String, Object> challenge4 = new HashMap<>();
        challenge4.put("id", 4);
        challenge4.put("name", "Tài chính");
        challenge4.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge4.put("currentDays", 10);
        challenge4.put("totalDays", 20);
        challenge4.put("streak", 10);
        challenge4.put("color", "purple");
        challenges.add(challenge4);

        Map<String, Object> challenge5 = new HashMap<>();
        challenge5.put("id", 4);
        challenge5.put("name", "Tài chính");
        challenge5.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge5.put("currentDays", 10);
        challenge5.put("totalDays", 20);
        challenge5.put("streak", 10);
        challenge5.put("color", "purple");
        challenges.add(challenge5);

        Map<String, Object> challenge6 = new HashMap<>();
        challenge6.put("id", 4);
        challenge6.put("name", "Tài chính");
        challenge6.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge6.put("currentDays", 10);
        challenge6.put("totalDays", 20);
        challenge6.put("streak", 10);
        challenge6.put("color", "purple");
        challenges.add(challenge6);

        Map<String, Object> challenge7 = new HashMap<>();
        challenge7.put("id", 4);
        challenge7.put("name", "Tài chính");
        challenge7.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge7.put("currentDays", 10);
        challenge7.put("totalDays", 20);
        challenge7.put("streak", 10);
        challenge7.put("color", "purple");
        challenges.add(challenge7);

        Map<String, Object> challenge8 = new HashMap<>();
        challenge8.put("id", 4);
        challenge8.put("name", "Tài chính");
        challenge8.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge8.put("currentDays", 10);
        challenge8.put("totalDays", 20);
        challenge8.put("streak", 10);
        challenge8.put("color", "purple");
        challenges.add(challenge8);

        Map<String, Object> challenge9 = new HashMap<>();
        challenge9.put("id", 4);
        challenge9.put("name", "Tài chính");
        challenge9.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge9.put("currentDays", 10);
        challenge9.put("totalDays", 20);
        challenge9.put("streak", 10);
        challenge9.put("color", "purple");
        challenges.add(challenge9);

        Map<String, Object> challenge10 = new HashMap<>();
        challenge10.put("id", 4);
        challenge10.put("name", "Tài chính");
        challenge10.put("description", "Mô tả: Quản lý chi và tiêu một hợp lý");
        challenge10.put("currentDays", 10);
        challenge10.put("totalDays", 20);
        challenge10.put("streak", 10);
        challenge10.put("color", "purple");
        challenges.add(challenge10);

        model.addAttribute("challenges", userChallenges);


        return "client/challenge";
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}

