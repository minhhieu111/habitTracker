package com.example.habittracker.Service;

import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.HabitRepository;
import com.example.habittracker.Repository.UserHabitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final UserService userService;
    private final UserHabitRepository userHabitRepository;

    public HabitService(HabitRepository habitRepository, UserService userService, UserHabitRepository userHabitRepository) {
        this.habitRepository = habitRepository;
        this.userService = userService;
        this.userHabitRepository = userHabitRepository;
    }

    @Transactional
    public List<UserHabit> getUserHabits(User user) {
        return this.userHabitRepository.findHabitsForUser(user.getUserId()).get();
    }

    @Transactional
    public Habit getHabit(Long habitId) {
        return this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Không tìm thấy thói quen!"));
    }

    @Transactional
    public void save(HabitDTO habitDTO, String username) {
        User user = this.userService.getUser(username);

        if(habitDTO.getTitle().equals("")){
            throw new RuntimeException("Tạo Thói quen thất Bại! Tiêu đề không được để trống");
        }
        if(habitDTO.getTargetCount() < 1 || habitDTO.getTargetCount() == null){
            throw new RuntimeException("Tạo Thói quen thất Bại! Mục tiêu không được để trống và mục tiêu phải lớn hơn 1");
        }
        Habit createHabit = new Habit();
        createHabit.setTitle(habitDTO.getTitle());
        createHabit.setDescription(habitDTO.getDescription());
        createHabit.setType(habitDTO.getType());
        createHabit.setDifficulty(habitDTO.getDifficulty());
        this.habitRepository.save(createHabit);

        UserHabit userHabit = new UserHabit();
        userHabit.setUser(user);
        userHabit.setHabit(createHabit);
        userHabit.setTargetCount(habitDTO.getTargetCount());
        userHabit.setCurrentCount(0L);
        this.userHabitRepository.save(userHabit);
    }

    @Transactional
    public HabitDTO getUpdateHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Lỗi khi chỉnh sửa thói quen!"));
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi lấy dữ liệu chỉnh sửa!"));
        HabitDTO habitDTO = new HabitDTO().builder()
                .title(userHabit.getHabit().getTitle())
                .description(userHabit.getHabit().getDescription())
                .type(userHabit.getHabit().getType())
                .difficulty(userHabit.getHabit().getDifficulty())
                .targetCount(userHabit.getTargetCount())
                .challengeId(userHabit.getHabit().getChallengeId())
                .build();
        return habitDTO;
    }

    @Transactional
    public void updateHabit(HabitDTO habitDTO, String username) {
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitDTO.getHabitId());

        if(habitDTO.getTitle().equals("")){
            throw new RuntimeException("Tạo Thói quen thất Bại! Tiêu đề không được để trống");
        }
        if(habitDTO.getTargetCount() < 1 || habitDTO.getTargetCount() == null){
            throw new RuntimeException("Tạo Thói quen thất Bại! Mục tiêu không được để trống và mục tiêu phải lớn hơn 1");
        }

        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setDifficulty(habitDTO.getDifficulty());
        habit.setType(habitDTO.getType());
        habit.setChallengeId(habitDTO.getChallengeId());

        this.habitRepository.save(habit);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi khi lưu dữ liệu chỉnh sửa!"));
        userHabit.setTargetCount(habitDTO.getTargetCount());
        this.userHabitRepository.save(userHabit);
    }

    @Transactional
    public void deleteHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitId);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi xảy ra khi xóa thói quen!"));
        this.userHabitRepository.delete(userHabit);
        this.habitRepository.delete(habit);
    }
}
