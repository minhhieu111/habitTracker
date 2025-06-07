package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.MessageResponse;
import com.example.habittracker.Domain.Challenge;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Repository.ChallengeRepository;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/challenge_community")
public class ChallengeCommunityController {
    private final ChallengeService challengeService;
    private final ChallengeRepository challengeRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;

    public ChallengeCommunityController(ChallengeService challengeService, ChallengeRepository challengeRepository, UserService userService, JwtUtil jwtUtil, TokenUtil tokenUtil) {
        this.challengeService = challengeService;
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("")
    public String challengeCommunityPage(Model model){
        List<UserChallenge> sharedChallenge = this.challengeService.getSharedChallenge();

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("months", new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});

        model.addAttribute("challengeShare", sharedChallenge);
        model.addAttribute("totalPosts", sharedChallenge.size());
        return "client/challengeCommunity";
    }

    @GetMapping("/join_challenge/{challengeId}")
    @ResponseBody
    public ResponseEntity<MessageResponse> joinChallenge(HttpServletRequest request, @PathVariable("challengeId") Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách "));

        try {
            User user = getUserFromRequest(request);
            this.challengeService.joinChallenge(user, challenge);
        }catch (Exception e){
            MessageResponse messageResponse = MessageResponse.builder()
                    .type("error")
                    .message("Tham gia thử thách " + challenge.getTitle() + " không thành công!")
                    .build();
            System.out.println(e.getMessage());
            return ResponseEntity.ok().body(messageResponse);
        }

        MessageResponse messageResponse = MessageResponse.builder()
                .type("success")
                .message("Bạn đã tham gia thử thách " + challenge.getTitle() + " thành công!")
                .build();
        return ResponseEntity.ok().body(messageResponse);
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }

}
