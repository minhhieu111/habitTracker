package com.example.habittracker.Service;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.DTO.Login;
import com.example.habittracker.DTO.Register;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;
    private String folder = "user_avatar";
    public static String defaultPassAuth = "oauth2_user_password";

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;
    }

    public void register(Register register) {
        if (userRepository.existsUserByEmail(register.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp");
        }

        User user = User.builder()
                .userName(register.getUsername())
                .password(passwordEncoder.encode(register.getPassword()))
                .email(register.getEmail())
                .role(User.Role.USER)
                .coins(0L)
                .build();

        userRepository.save(user);
    }

    public User login(Login login) {
        User user = userRepository.findByEmail(login.getEmail());

        if(user == null){
            throw new RuntimeException("Không tìm thấy Email!");
        }
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        user.setToken(token);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User createOAuth2User(String username, String email, String avatar){
        User user = User.builder()
                .userName(username)
                .email(email)
                .password(passwordEncoder.encode(defaultPassAuth))
                .role(User.Role.USER)
                .coins(0L)
                .limitCoinsEarnedPerDay(0L)
                .build();

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String filePath = imageService.saveImageFromUrl(avatar, folder);
                user.setAvatar(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage(), e);
            }
        }
        this.userRepository.save(user);
        return user;
    }
}
