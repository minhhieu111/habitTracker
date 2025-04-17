package com.example.habittracker.Service;

import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public List<Habit> getUserHabits(User user) {
        return this.habitRepository.findAllHabitsForUser(user.getUserId());
    }
}
