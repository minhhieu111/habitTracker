package com.example.habittracker.Service;

import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.DailyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyService {
    private final DailyRepository dailyRepository;
    public DailyService(DailyRepository dailyRepository) {
        this.dailyRepository = dailyRepository;
    }

    public List<UserDaily> getUserDaily(User user){
        return this.dailyRepository.findUserDailiesByUserId(user.getUserId()).get();
    }
}
