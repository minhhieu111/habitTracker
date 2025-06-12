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
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final AchievementService achievementService;

    public SchedulerConfig(HabitService habitService, DailyService dailyService, ChallengeService challengeService, UserRepository userRepository, EmailService emailService, UserService userService, AchievementService achievementService) {
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.challengeService = challengeService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userService = userService;
        this.achievementService = achievementService;
    }


//@Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron ="58 59 23 * * *")
    public void ResetHabitCountAndDailyAndLimitCoinsEarned() {
        this.dailyService.resetDaily();
        this.habitService.resetHabit();
        this.userService.resetLimitCoin();
    }

   @Scheduled(cron="0 59 23 * * *")
    public void calChallengeProgress(){
        this.challengeService.CalChallengeProgressEndDay();
   }

   @Scheduled(cron="30 59 23 * * *")
   public void achievementCheck(){
       List<User> users = userRepository.findAll();
       for (User user : users) {
           this.achievementService.receiveAchievement(user);
       }
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
