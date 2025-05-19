package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.DiaryDTO;
import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Service.DiaryService;
import com.example.habittracker.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/diaries")
public class DiaryController {
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final DiaryService diaryService;
    private final UserService userService;

    public DiaryController(TokenUtil tokenUtil, JwtUtil jwtUtil, DiaryService diaryService, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.diaryService = diaryService;
        this.userService = userService;
    }

    @GetMapping("/{diaryId}")
    @ResponseBody
    public DiaryDTO getDiary(@PathVariable Long diaryId) {
        return diaryService.getDiaryDTO(diaryId);
    }

    @PostMapping("/save")
    public String saveDiary(@ModelAttribute("newDiary") DiaryDTO diaryDTO, HttpServletRequest request, @RequestParam("image") MultipartFile image) {
         User user = getUserFromRequest(request);
        diaryService.saveDiary(diaryDTO, image, user);
        return "redirect:/overview";
    }

    @PostMapping("/update")
    public String updateDiary(@ModelAttribute("newDiary") DiaryDTO diaryDTO, @RequestParam(value = "image", required = false) MultipartFile image) {
        diaryService.updateDiary(diaryDTO, image);
        return "redirect:/overview";
    }

    @PostMapping("/{diaryId}/delete")
    public String deleteDiary(@PathVariable Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return "redirect:/overview";
    }

    @PostMapping("/{diaryId}/update-tasks")
    @ResponseBody
    public void updateDiaryTasks(@PathVariable Long diaryId, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        diaryService.updateDiaryTasks(diaryId, user);
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String username =  this.jwtUtil.getUserNameFromToken(token);
        return this.userService.getUser(username);
    }
}