package com.example.habittracker.Service;

import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.HabitRepository;
import com.example.habittracker.Repository.UserHabitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public List<UserHabit> getUserHabits(User user) {
        return this.userHabitRepository.findHabitsForUser(user.getUserId()).get();
    }

    @Transactional
    public void save(HabitDTO habitDTO, String username) {
        User user = this.userService.getUser(username);

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
}
