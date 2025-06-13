package com.example.habittracker.Controller.admin;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.Auth.TokenUtil;
import com.example.habittracker.Domain.User;
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
@RequestMapping("/admin")
public class PostManagementController {
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public PostManagementController(TokenUtil tokenUtil, JwtUtil jwtUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public String postManagement(HttpServletRequest request ,Model model) {
        User userAdmin = getUserAdmin(request);
        model.addAttribute("userAdmin", userAdmin);
        // Published posts data
        List<Map<String, Object>> publishedPosts = new ArrayList<>();

        Map<String, Object> post1 = new HashMap<>();
        post1.put("id", 1);
        post1.put("authorInitials", "NA");
        post1.put("authorName", "Nguyễn Văn A");
        post1.put("date", "2024-06-13 09:30");
        post1.put("title", "30 Ngày Tập Thể Dục");
        post1.put("content", "Hôm nay tôi đã hoàn thành ngày thứ 15 của thử thách. Cảm thấy rất tuyệt vời và có nhiều năng lượng hơn!");
        post1.put("streak", "15/30");
        post1.put("completion", 50);
        post1.put("likes", 12);
        post1.put("comments", 3);
        publishedPosts.add(post1);

        Map<String, Object> post2 = new HashMap<>();
        post2.put("id", 2);
        post2.put("authorInitials", "TB");
        post2.put("authorName", "Trần Thị B");
        post2.put("date", "2024-06-12 14:45");
        post2.put("title", "21 Ngày Đọc Sách");
        post2.put("content", "Vừa hoàn thành cuốn sách thứ 8 trong thử thách 21 ngày đọc sách. Kiến thức thật sự rất bổ ích!");
        post2.put("streak", "18/21");
        post2.put("completion", 85);
        post2.put("likes", 25);
        post2.put("comments", 7);
        publishedPosts.add(post2);

        Map<String, Object> post3 = new HashMap<>();
        post3.put("id", 3);
        post3.put("authorInitials", "LC");
        post3.put("authorName", "Lê Minh C");
        post3.put("date", "2024-06-12 08:15");
        post3.put("title", "14 Ngày Thiền Định");
        post3.put("content", "Thiền định giúp tôi tĩnh tâm và tập trung tốt hơn. Ngày hôm nay đã thiền được 20 phút liền tục.");
        post3.put("streak", "12/14");
        post3.put("completion", 85);
        post3.put("likes", 8);
        post3.put("comments", 2);
        publishedPosts.add(post3);

        Map<String, Object> post4 = new HashMap<>();
        post4.put("id", 4);
        post4.put("authorInitials", "PD");
        post4.put("authorName", "Phạm Thị D");
        post4.put("date", "2024-06-11 16:20");
        post4.put("title", "30 Ngày Tập Thể Dục");
        post4.put("content", "Ngày thứ 5 của thử thách. Hôm nay tập luyện khá vất vả nhưng cảm thấy rất hài lòng với kết quả.");
        post4.put("streak", "5/30");
        post4.put("completion", 16);
        post4.put("likes", 4);
        post4.put("comments", 1);
        publishedPosts.add(post4);

        model.addAttribute("publishedPosts", publishedPosts);

        // Pending posts data
        List<Map<String, Object>> pendingPosts = new ArrayList<>();

        Map<String, Object> pendingPost1 = new HashMap<>();
        pendingPost1.put("id", 5);
        pendingPost1.put("authorName", "Nguyễn Văn A");
        pendingPost1.put("title", "30 Ngày Tập Thể Dục");
        pendingPosts.add(pendingPost1);

        Map<String, Object> pendingPost2 = new HashMap<>();
        pendingPost2.put("id", 6);
        pendingPost2.put("authorName", "Trần Thị B");
        pendingPost2.put("title", "30 Ngày Tập Thể Dục");
        pendingPosts.add(pendingPost2);

        Map<String, Object> pendingPost3 = new HashMap<>();
        pendingPost3.put("id", 7);
        pendingPost3.put("authorName", "Lê Văn C");
        pendingPost3.put("title", "30 Ngày Tập Thể Dục");
        pendingPosts.add(pendingPost3);

        Map<String, Object> pendingPost4 = new HashMap<>();
        pendingPost4.put("id", 8);
        pendingPost4.put("authorName", "Phạm Thị D");
        pendingPost4.put("title", "30 Ngày Tập Thể Dục");
        pendingPosts.add(pendingPost4);

        model.addAttribute("pendingPosts", pendingPosts);
        model.addAttribute("pendingCount", pendingPosts.size());

        return "admin/postmanage";
    }
    public User getUserAdmin(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String email = jwtUtil.getEmailFromToken(token);

        return this.userService.getUser(email);
    }
}
