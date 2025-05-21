package com.example.habittracker.Config;

import com.example.habittracker.Repository.HabitRepository;
import com.example.habittracker.Service.DailyService;
import com.example.habittracker.Service.HabitService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final HabitService habitService;
    private final DailyService dailyService;

    public SchedulerConfig(HabitService habitService, DailyService dailyService) {
        this.habitService = habitService;
        this.dailyService = dailyService;
    }

    @Scheduled(cron ="59 59 23 * * *")
//@Scheduled(cron = "0 * * * * *")
    public void ResetHabitCount(){
        this.habitService.resetHabit();
    }

//   @Scheduled(cron = "0 * * * * *")
   @Scheduled(cron ="59 59 23 * * *")
    public void ResetDaily(){this.dailyService.resetDaily();}
}
