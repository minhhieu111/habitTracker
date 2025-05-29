package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.ChallengeDTO;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;
    private final JwtUtil jwtUtil;
    private final TokenUtil tokenUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public ChallengeController(ChallengeService challengeService, JwtUtil jwtUtil, TokenUtil tokenUtil, UserService userService, ObjectMapper objectMapper) {
        this.challengeService = challengeService;
        this.jwtUtil = jwtUtil;
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/save")
    public String createChallenge(HttpServletRequest request,@RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  @RequestParam("day") Long day,
                                  @RequestParam("habits") String habitsJson,
                                  @RequestParam("dailies") String dailiesJson, RedirectAttributes redirectAttributes) throws Exception{
        try{
            List<HabitDTO> habits = objectMapper.readValue(habitsJson, new TypeReference<List<HabitDTO>>() {});
            List<DailyDTO> dailies = objectMapper.readValue(dailiesJson, new TypeReference<List<DailyDTO>>() {});

            ChallengeDTO challengeDTO = ChallengeDTO.builder()
                    .title(title)
                    .description(description)
                    .endDate(endDate)
                    .day(day)
                    .habits(habits)
                    .dailies(dailies)
                    .build();

            User user = getUserFromRequest(request);
            this.challengeService.createChallenge(challengeDTO, user);
            redirectAttributes.addFlashAttribute("success", "Tạo Thử Thách Thành Công!");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("fail", e.getMessage());
            return "redirect:/challenge_overview";
        }
        return "redirect:/challenge_overview";
    }


    //Complete challenge
//    @GetMapping("/completed")
//    public ResponseEntity<List<ChallengeDTO>> getCompletedChallenges(Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            List<ChallengeDTO> completedChallenges = challengeService.getCompletedChallenges(username);
//            return ResponseEntity.ok(completedChallenges);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//    @PostMapping("/complete/{challengeId}")
//    public ResponseEntity<?> completeChallenge(@PathVariable Long challengeId, Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            challengeService.completeChallenge(challengeId, username);
//            return ResponseEntity.ok(new Response("Hoàn thành và chia sẻ thử thách thành công!", true));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new Response(e.getMessage(), false));
//        }
//    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}
