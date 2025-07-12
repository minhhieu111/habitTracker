package com.example.habittracker.Config;

import com.example.habittracker.Service.ChallengeProgressService;
import com.example.habittracker.Service.ChallengeService;
import com.example.habittracker.Service.DailyService;
import com.example.habittracker.Service.HabitService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerConfig {
    private final HabitService habitService;
    private final DailyService dailyService;
    private final ChallengeService challengeService;
    private final ChallengeProgressService challengeProgressService;

    public SchedulerConfig(HabitService habitService, DailyService dailyService, ChallengeService challengeService, ChallengeProgressService challengeProgressService) {
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.challengeService = challengeService;
        this.challengeProgressService = challengeProgressService;
    }


//@Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron ="58 59 23 * * *")
    public void ResetHabitCount(){
        this.habitService.resetHabit();
    }

    @Scheduled(cron ="58 59 23 * * *")
    public void ResetDaily(){
        this.dailyService.resetDaily();
   }

   @Scheduled(cron="0 59 23 * * *")
    public void calChallengeProgress(){
        this.challengeService.CalChallengeProgressEndDay();
   }

   @Scheduled(cron="1 0 0 * * *")
    public void setTaskHistory(){
        this.habitService.setHabitHistoryNewDay();
        this.dailyService.setDailyHistoryNewDay();
   }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAllChallenges() {
        this.challengeService.checkChallengeCompleted();
    }
}
