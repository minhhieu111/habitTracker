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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/challenge_overview";
        }
        return "redirect:/challenge_overview";
    }

    @GetMapping("/{challengeId}")
    @ResponseBody
    public ResponseEntity<ChallengeDTO> getChallenge(@PathVariable Long challengeId) {
        ChallengeDTO challengeDTO = challengeService.getChallengeById(challengeId);
        if (challengeDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(challengeDTO);
    }

    @PostMapping("/{challengeId}")
    public String updateChallenge(HttpServletRequest request,
                                  @RequestParam("challengeId") Long challengeId,
                                  @RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  @RequestParam("day") Long day,
                                  @RequestParam("habits") String habitsJson,
                                  @RequestParam("dailies") String dailiesJson, RedirectAttributes redirectAttributes) throws Exception{
        try{
            List<HabitDTO> habits = objectMapper.readValue(habitsJson, new TypeReference<List<HabitDTO>>() {});
            List<DailyDTO> dailies = objectMapper.readValue(dailiesJson, new TypeReference<List<DailyDTO>>() {});

            ChallengeDTO challengeDTO = ChallengeDTO.builder()
                    .challengeId(challengeId)
                    .title(title)
                    .description(description)
                    .endDate(endDate)
                    .day(day)
                    .habits(habits)
                    .dailies(dailies)
                    .build();

            User user = getUserFromRequest(request);
            this.challengeService.updateChallenge(challengeDTO, user);
            redirectAttributes.addFlashAttribute("success", "Chỉnh sửa Thử Thách Thành Công!");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/challenge_overview";
        }
        return "redirect:/challenge_overview";
    }

    @GetMapping("/delete/{challengeId}")
    public String deleteChallenge(HttpServletRequest request,@PathVariable Long challengeId, RedirectAttributes redirectAttributes) {
        try{
            User user = getUserFromRequest(request);
            this.challengeService.deleteChallenge(challengeId,user);
            redirectAttributes.addFlashAttribute("success", "Xóa thử thách thành công!");
        }catch(RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
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

    @GetMapping("/detail/challenge/{id}")
    @ResponseBody
    public Map<String, Object> getChallengeDetails(@PathVariable Long id) {


        Map<String, Object> response = new HashMap<>();

        // Challenge details
        response.put("id", id);
        response.put("name", "Thay Đổi Lối Sống");
        response.put("description", "Thay đổi thói quen trở nên lành mạnh");
        response.put("bestStreak", 5);
        response.put("completionRate", 74);
        response.put("totalHabitsCompleted", 42);

        // Line chart data
        List<Map<String, Object>> lineChartData = new ArrayList<>();
        String[] dates = {"Apr 21", "Apr 25", "Apr 30", "May 05", "May 10"};
        int[] values = {60, 35, 50, 90, 30};

        for (int i = 0; i < dates.length; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", dates[i]);
            point.put("value", values[i]);
            lineChartData.add(point);
        }
        response.put("lineChartData", lineChartData);

        // Donut chart data
        Map<String, Object> donutChartData = new HashMap<>();
        donutChartData.put("completed", 74);
        donutChartData.put("remaining", 26);
        response.put("donutChartData", donutChartData);

        // Calendar data
        List<String> completedDates = new ArrayList<>();
        completedDates.add("2025-05-02");
        completedDates.add("2025-05-03");
        completedDates.add("2025-05-05");
        completedDates.add("2025-05-06");
        completedDates.add("2025-05-07");
        completedDates.add("2025-05-08");
        completedDates.add("2025-05-10");
        completedDates.add("2025-05-11");
        completedDates.add("2025-05-14");
        completedDates.add("2025-05-16");
        completedDates.add("2025-05-18");
        response.put("completedDates", completedDates);

        return response;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}
