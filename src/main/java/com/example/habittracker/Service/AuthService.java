package com.example.habittracker.Service;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.DTO.Login;
import com.example.habittracker.DTO.Register;
import com.example.habittracker.Domain.Achievement;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserAchievement;
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
    private final AchievementService achievementService;
    private String folder = "user_avatar";
    public static String defaultPassAuth = "oauth2_user_password";
    private final Long coinsDefault = 0L;
    private final Long challengeLimitDefault = 3L;
    private final Long taskLimitDefault = 10L;
    private final Long limitCoinsEarnedPerDayDefault = 10L;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ImageService imageService, AchievementService achievementService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;
        this.achievementService = achievementService;
    }

    public void register(Register register) {
        if (userRepository.existsUserByEmail(register.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp");
        }

        Achievement achievement = this.achievementService.getAchievementById(1L);

        User user = User.builder()
                .userName(register.getUsername())
                .password(passwordEncoder.encode(register.getPassword()))
                .email(register.getEmail())
                .role(User.Role.USER)
                .coins(coinsDefault)
                .challengeLimit(challengeLimitDefault)
                .taskLimit(taskLimitDefault)
                .limitCoinsEarnedPerDay(limitCoinsEarnedPerDayDefault)
                .createAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        UserAchievement userAchievement = UserAchievement.builder().user(user).achievement(achievement).earnedDate(LocalDateTime.now()).isNotification(false).build();

        this.achievementService.saveUserAchievement(userAchievement);


    }

    public User login(Login login) {
        User user = userRepository.findByEmail(login.getEmail()).orElseThrow(()->new RuntimeException("Không tìm thấy Email!"));
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
                .coins(coinsDefault)
                .challengeLimit(challengeLimitDefault)
                .taskLimit(taskLimitDefault)
                .limitCoinsEarnedPerDay(limitCoinsEarnedPerDayDefault)
                .createAt(LocalDateTime.now())
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
        Achievement achievement = this.achievementService.getAchievementById(1L);

        UserAchievement userAchievement = UserAchievement.builder().user(user).achievement(achievement).earnedDate(LocalDateTime.now()).isNotification(false).build();
        this.achievementService.saveUserAchievement(userAchievement);


        return user;
    }
}
