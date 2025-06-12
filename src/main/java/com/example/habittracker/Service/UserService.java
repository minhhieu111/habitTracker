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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private String folder = "user_avatar";
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
    public User getUser(String email) {
        return this.userRepository.findByEmail(email);
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

    public long getTaskComplete(User user, boolean inChallenge){
        List<Todo> todos;
        List<UserDaily> userDailies;
        List<UserHabit> userHabits;

        if(inChallenge){
            todos = this.todoRepository.findAllByUser(user);
            userDailies = this.userDailyRepository.findByUserId(user.getUserId());
            userHabits = this.userHabitRepository.findHabitsForUser(user.getUserId());
        }else{
            todos = this.todoRepository.findByUser(user);
            userDailies = this.userDailyRepository.findByUserAndNotInChallengeAndUnavailableFalse(user);
            userHabits = this.userHabitRepository.findByUserAndNotInChallengeAndUnavailableFalse(user);
        }


        long totalCompleteTask = 0;
        for (Todo todo : todos) {
            totalCompleteTask += this.todoHistoryRepository.countCompleteTask(todo);
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
        user.setAchieveTitle(userUpdateInfo.getAchieveTitle());

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

    public UserDTO UserChangePassword(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setJustLoginWithGoogle(user.getPassword() != null && passwordEncoder.matches(AuthService.defaultPassAuth,user.getPassword()));
        return userDTO;
    }

    public void changePassword(UserDTO userDTO) {
        User user = this.userRepository.findById(userDTO.getUserId()).get();

        //lỗi khi người dùng tạo tk bằng gg xog đổi mật khẩu thành công mà không load lại trang thì chỉ hiển thị 2 input
        if(passwordEncoder.matches(AuthService.defaultPassAuth,user.getPassword())){
            if(userDTO.getPassword().equals(userDTO.getConfirmPassword())){
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }else{
                throw new RuntimeException("Mật khẩu và mật khẩu xác nhận không khớp!");
            }
        } else if (userDTO.getOldPassword()!=null && passwordEncoder.matches(userDTO.getOldPassword(),user.getPassword())) {
            if(userDTO.getPassword().equals(userDTO.getConfirmPassword())){
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }else{
                throw new RuntimeException("Mật khẩu và mật khẩu xác nhận không khớp!");
            }
        }else {
            throw new RuntimeException("Mật khẩu cũ không khớp!");
        }
        this.userRepository.save(user);
    }
}
