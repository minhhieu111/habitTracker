package com.example.habittracker.Config;

import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.UserRepository;
import com.example.habittracker.Service.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Configuration
public class SchedulerConfig {
    private final HabitService habitService;
    private final DailyService dailyService;
    private final ChallengeService challengeService;
    private final ChallengeProgressService challengeProgressService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public SchedulerConfig(HabitService habitService, DailyService dailyService, ChallengeService challengeService, ChallengeProgressService challengeProgressService, UserRepository userRepository, EmailService emailService) {
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.challengeService = challengeService;
        this.challengeProgressService = challengeProgressService;
        this.userRepository = userRepository;
        this.emailService = emailService;
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

    @Scheduled(cron = "0 0 21 * * ?")
    public void sendUncompletedTasksEmailForAllUsers() {
        List<User> users = userRepository.findAll();

        users.forEach(user -> {
            List<UserHabit> userHabitsUnComplete = this.habitService.getHabitIsUnComplete(user);
            List<UserDaily> userDailiesUnComplete = this.dailyService.getDailyUnCompleteEnableToday(user);
            this.emailService.sendEmailTaskUnComplete(userDailiesUnComplete,userHabitsUnComplete, user);
        });
    }
}
