package com.example.habittracker.Service;

import com.example.habittracker.DTO.UserChallengeStats;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private String folder = "user_avatar";
    private String defaultPassAuth = "oauth2_user_password";

    public UserService(UserRepository userRepository, ImageService imageService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User getUser(String username) {
        return this.userRepository.findUserByUserName(username).orElseThrow(()->new RuntimeException("Không tìm thấy người dùng!"));
    }

    @Transactional
    public User getUserById(Long id) {
        return this.userRepository.findById(id).get();
    }

    @Transactional
    public User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
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

    @Transactional
    public List<UserChallengeStats> getUsersAndCompletedChallenges() {
        return this.userRepository.findAllUsersOrderedByCompletedChallenges();
    }

    @Transactional
    public String getCoinComplete(User user, Long coinEarn) {
        String message = "";
        if(user.getCoins() >= 0 && coinEarn > 0){
            if(user.getLimitCoinsEarnedPerDay() < 500){
                user.setCoins(user.getCoins()+coinEarn);
                user.setLimitCoinsEarnedPerDay(user.getLimitCoinsEarnedPerDay()+coinEarn);
                message = "+"+coinEarn;
            }else{
                message = "Bạn đã đạt giới hạn nhận xu ngày hôm này là 500 xu";
            }
        }else{
            user.setCoins(user.getCoins()+coinEarn);
            user.setLimitCoinsEarnedPerDay(user.getLimitCoinsEarnedPerDay()+coinEarn);
            message = ""+coinEarn;
        }
        this.userRepository.save(user);
        return message;
    }

    public void resetLimitCoin() {
        List<User> users = this.userRepository.findAll().stream().map(user -> {
            user.setLimitCoinsEarnedPerDay(0L);
            return user;
        }).collect(Collectors.toList());

        this.userRepository.saveAll(users);

    }
}
