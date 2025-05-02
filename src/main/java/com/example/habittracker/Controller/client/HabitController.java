package com.example.habittracker.Controller.client;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Service.HabitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habits")
public class HabitController {
    private final HabitService habitService;
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;

    public HabitController(HabitService habitService, TokenUtil tokenUtil, JwtUtil jwtUtil) {
        this.habitService = habitService;
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public String save(HttpServletRequest request, @ModelAttribute("newHabit") HabitDTO habitDTO, RedirectAttributes redirectAttributes) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        try{
            this.habitService.save(habitDTO,userName);
            redirectAttributes.addFlashAttribute("success", "Tạo thói quen thành công!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("failed", "Tạo thói quen thất bại!");
            return "redirect:/overview";
        }
        return "redirect:/overview";
    }

    @GetMapping("/{habitId}")
    @ResponseBody
    public ResponseEntity<HabitDTO> UpdateHabit(HttpServletRequest request, @PathVariable Long habitId) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        HabitDTO updateHabit = this.habitService.getUpdateHabit(habitId,userName);
        return ResponseEntity.ok().body(updateHabit);
    }

    @PostMapping("/update")
    public String updateHabit(HttpServletRequest request,@ModelAttribute("updateHabit") HabitDTO habitDTO, RedirectAttributes redirectAttributes) {
        try{
            String token = tokenUtil.getTokenFromCookies(request);
            String userName = jwtUtil.getUserNameFromToken(token);
            this.habitService.updateHabit(habitDTO, userName);
            redirectAttributes.addFlashAttribute("success", "Chỉnh sửa thành công!");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/overview";
        }
        return "redirect:/overview";
    }

    @GetMapping("/delete/{habitId}")
    public String deleteHabit(HttpServletRequest request, @PathVariable Long habitId, RedirectAttributes redirectAttributes) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        try{
            this.habitService.deleteHabit(habitId, userName);
            redirectAttributes.addFlashAttribute("success","Xóa thành công!");
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("failed", e.getMessage());
            return "redirect:/overview";
        }
        return "redirect:/overview";
    }

    @GetMapping("/{habitId}/progress_count")
    @ResponseBody
    public ResponseEntity<HabitDTO> progressCount(HttpServletRequest request,@PathVariable Long habitId, @RequestParam String type) {
        String token = tokenUtil.getTokenFromCookies(request);
        String userName = jwtUtil.getUserNameFromToken(token);
        HabitDTO habitProgress = this.habitService.updateHabitCount(habitId,userName,type);
        return ResponseEntity.ok().body(habitProgress);
    }
}
