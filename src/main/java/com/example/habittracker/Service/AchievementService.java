package com.example.habittracker.Service;

import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ChallengeService challengeService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository, UserAchievementRepository userAchievementRepository, ChallengeService challengeService, UserService userService, UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.challengeService = challengeService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Achievement getAchievementById(Long id) {
        return this.achievementRepository.getAchievementById(id);
    }

    @Transactional
    public void saveUserAchievement(UserAchievement userAchievement) {
        this.userAchievementRepository.save(userAchievement);
    }

    @Transactional
    public List<UserAchievement> getAchievementReceive(User user){
        return this.userAchievementRepository.getUserAchievementReceiveToday(LocalDate.now(),user);
    }

    @Transactional
    public List<UserAchievement> getUserAchievementReceive(User user){
        return this.userAchievementRepository.getUserAchievementReceive(user);
    }

    @Transactional
    public void receiveAchievement(User user) {

        long totalCompletedTask = this.userService.getTaskComplete(user,false);

        long totalChallengeValid= this.challengeService.getValidChallenges(user.getUserId()).size();

        List<Achievement> achievements = this.achievementRepository.findAll();

        for (Achievement achievement : achievements) {
            if(totalChallengeValid >0 && totalCompletedTask > 0){
                if(achievement.getRequiredChallenge()>=totalChallengeValid && achievement.getRequiredChallenge()>=totalCompletedTask){
                    UserAchievement userAchievement = UserAchievement.builder()
                            .user(user)
                            .achievement(achievement)
                            .earnedDate(LocalDateTime.now())
                            .build();
                    this.userAchievementRepository.save(userAchievement);

                    user.setChallengeLimit(achievement.getChallengeBonus()+ user.getChallengeLimit());
                    user.setTaskLimit(achievement.getTaskBonus()+ user.getTaskLimit());
                    user.setCoins(achievement.getCoinBonus()+user.getCoins());
                    this.userRepository.save(user);
                }
            } else if (achievement.getRequiredChallenge()>=totalChallengeValid && achievement.getRequiredTask()<=totalCompletedTask) {
                UserAchievement userAchievement = UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .earnedDate(LocalDateTime.now())
                        .build();
                this.userAchievementRepository.save(userAchievement);

                user.setTaskLimit(achievement.getTaskBonus()+ user.getTaskLimit());
                user.setCoins(achievement.getCoinBonus()+user.getCoins());
                this.userRepository.save(user);
            } else if (achievement.getRequiredChallenge()<=totalChallengeValid && achievement.getRequiredTask()>=totalCompletedTask){
                UserAchievement userAchievement = UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .earnedDate(LocalDateTime.now())
                        .build();
                this.userAchievementRepository.save(userAchievement);

                user.setChallengeLimit(achievement.getChallengeBonus()+ user.getChallengeLimit());
                user.setCoins(achievement.getCoinBonus()+user.getCoins());
                this.userRepository.save(user);
            }
        }
    }

}
