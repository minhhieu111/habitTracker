package com.example.habittracker.Service;

import com.example.habittracker.DTO.UserChallengeStats;
import com.example.habittracker.DTO.UserDTO;
import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final HabitHistoryRepository habitHistoryRepository;
    private final DailyHistoryRepository dailyHistoryRepository;
    private final TodoHistoryRepository todoHistoryRepository;
    private final UserDailyRepository userDailyRepository;
    private final UserHabitRepository userHabitRepository;
    private final TodoRepository todoRepository;

    public UserService(UserRepository userRepository, ImageService imageService, PasswordEncoder passwordEncoder, HabitHistoryRepository habitHistoryRepository, DailyHistoryRepository dailyHistoryRepository, TodoHistoryRepository todoHistoryRepository, UserDailyRepository userDailyRepository, UserHabitRepository userHabitRepository, TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.habitHistoryRepository = habitHistoryRepository;
        this.dailyHistoryRepository = dailyHistoryRepository;
        this.todoHistoryRepository = todoHistoryRepository;
        this.userDailyRepository = userDailyRepository;
        this.userHabitRepository = userHabitRepository;
        this.todoRepository = todoRepository;
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

    public Integer getUserRank(Long userId) {
        List<UserChallengeStats> usersStats = userRepository.findAllUsersOrderedByCompletedChallenges();
        for (int i = 0; i < usersStats.size(); i++) {
            UserChallengeStats stats = usersStats.get(i);
            if (stats.getUser().getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return null;
    }

    public long getTaskComplete(User user){
        List<Todo> todos = this.todoRepository.findAllByUser(user);
        List<UserDaily> userDailies = this.userDailyRepository.findByUserId(user.getUserId());
        List<UserHabit> userHabits = this.userHabitRepository.findHabitsForUser(user.getUserId());

        long totalCompleteTask = 0;
        for (Todo todo : todos) {
            totalCompleteTask += this.todoHistoryRepository.getCompleteTask(todo);
        }

        for (UserDaily userDaily : userDailies) {
            totalCompleteTask += this.dailyHistoryRepository.countCompleteDaily(userDaily);
        }

        for (UserHabit userHabit : userHabits) {
            totalCompleteTask += this.habitHistoryRepository.countCompleteHabit(userHabit);
        }

        return totalCompleteTask;
    }

    public void updateUser(User userUpdateInfo, MultipartFile image) {
        User user = this.userRepository.findById(userUpdateInfo.getUserId()).orElseThrow(()->new RuntimeException("Không tìm thấy người dùng!"));

        user.setUserName(userUpdateInfo.getUserName());

        if (image != null && !image.isEmpty()) {
            try {
                String filePath = imageService.saveImage(image, folder);
                user.setAvatar(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage(), e);
            }
        }

        this.userRepository.save(user);
    }
}
