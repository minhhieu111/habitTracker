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
public class UserManagementController {
    private final TokenUtil tokenUtil;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public UserManagementController(TokenUtil tokenUtil, JwtUtil jwtUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String userManagement(HttpServletRequest request, Model model) {
        User userAdmin = getUserAdmin(request);
        model.addAttribute("userAdmin", userAdmin);
        // Stats data
        model.addAttribute("totalUsers", 2456);
        model.addAttribute("revenue", 34252);
        model.addAttribute("totalBets", 12345);
        model.addAttribute("newUsers", 128);

        model.addAttribute("userGrowth", 12);
        model.addAttribute("revenueGrowth", 8.3);
        model.addAttribute("betsGrowth", -3.2);
        model.addAttribute("newUsersGrowth", 18.7);

        // Booking list data
        List<Map<String, Object>> bookings = new ArrayList<>();

        Map<String, Object> booking1 = new HashMap<>();
        booking1.put("id", "235645");
        booking1.put("clientName", "Jerome Bell");
        booking1.put("bookingDate", "2:00 PM, Tue, 13 Nov");
        booking1.put("providerName", "Ronald Richards");
        booking1.put("service", "Relax & Rejuvenate: Salon");
        booking1.put("status", "Confirmed");
        bookings.add(booking1);

        Map<String, Object> booking2 = new HashMap<>();
        booking2.put("id", "235646");
        booking2.put("clientName", "Albert Flores");
        booking2.put("bookingDate", "4:00 PM, Tue, 12 Nov");
        booking2.put("providerName", "Leslie Alexander");
        booking2.put("service", "Back Care Combo");
        booking2.put("status", "Confirmed");
        bookings.add(booking2);

        Map<String, Object> booking3 = new HashMap<>();
        booking3.put("id", "235648");
        booking3.put("clientName", "Leslie Alexander");
        booking3.put("bookingDate", "3:00 PM, Tue, 11 Nov");
        booking3.put("providerName", "Kathryn Murphy");
        booking3.put("service", "Revive Expert Salon");
        booking3.put("status", "Confirmed");
        bookings.add(booking3);

        Map<String, Object> booking4 = new HashMap<>();
        booking4.put("id", "235649");
        booking4.put("clientName", "Theresa Webb");
        booking4.put("bookingDate", "1:00 PM, Tue, 10 Nov");
        booking4.put("providerName", "Jerome Bell");
        booking4.put("service", "Nails, Right Fingertips");
        booking4.put("status", "Confirmed");
        bookings.add(booking4);

        Map<String, Object> booking5 = new HashMap<>();
        booking5.put("id", "235650");
        booking5.put("clientName", "Devon Lane");
        booking5.put("bookingDate", "11:00 PM, Tue, 9 Nov");
        booking5.put("providerName", "Esther Howard");
        booking5.put("service", "Glow Home: Professional");
        booking5.put("status", "Canceled");
        bookings.add(booking5);

        Map<String, Object> booking6 = new HashMap<>();
        booking6.put("id", "235651");
        booking6.put("clientName", "Marvin McKinney");
        booking6.put("bookingDate", "12:00 PM, Tue, 7 Nov");
        booking6.put("providerName", "Bessie Cooper");
        booking6.put("service", "Gorgeous Nails: Decor");
        booking6.put("status", "Confirmed");
        bookings.add(booking6);

        Map<String, Object> booking7 = new HashMap<>();
        booking7.put("id", "235652");
        booking7.put("clientName", "Cameron Williamson");
        booking7.put("bookingDate", "2:00 PM, Tue, 7 Nov");
        booking7.put("providerName", "Marvin McKinney");
        booking7.put("service", "Gorgeous Nails: Decor");
        booking7.put("status", "Confirmed");
        bookings.add(booking7);

        Map<String, Object> booking8 = new HashMap<>();
        booking8.put("id", "235653");
        booking8.put("clientName", "Brooklyn Simmons");
        booking8.put("bookingDate", "3:00 PM, Tue, 6 Nov");
        booking8.put("providerName", "Guy Hawkins");
        booking8.put("service", "Tranquility Delivered: Salon");
        booking8.put("status", "Confirmed");
        bookings.add(booking8);

        model.addAttribute("bookings", bookings);

        return "admin/usermanage";
    }
    public User getUserAdmin(HttpServletRequest request) {
        String token = tokenUtil.getTokenFromCookies(request);
        String email = jwtUtil.getEmailFromToken(token);

        return this.userService.getUser(email);
    }
}
