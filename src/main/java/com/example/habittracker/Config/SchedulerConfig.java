package com.example.habittracker.Config;

import com.example.habittracker.Repository.HabitRepository;
import com.example.habittracker.Service.HabitService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final HabitService habitService;

    public SchedulerConfig(HabitService habitService) {
        this.habitService = habitService;
    }

    @Scheduled(cron ="0 * * * * *")
    public void ResetHabitCount(){
        this.habitService.resetHabit();
    }
}
