package com.example.habittracker.Service;

import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.DailyRepository;
import com.example.habittracker.Repository.UserDailyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DailyService {
    private final DailyRepository dailyRepository;
    private final UserService userService;
    private final UserDailyRepository userDailyRepository;
    public DailyService(DailyRepository dailyRepository, UserService userService, UserDailyRepository userDailyRepository) {
        this.dailyRepository = dailyRepository;
        this.userService = userService;
        this.userDailyRepository = userDailyRepository;
    }

    public List<UserDaily> getUserDaily(User user){
        return this.dailyRepository.findUserDailiesByUserId(user.getUserId()).get();
    }

    @Transactional
    public void createDaily(DailyDTO dailyDTO, String username) {

        User creator = userService.getUser(username);

        Daily daily = Daily.builder()
                .title(dailyDTO.getTitle())
                .description(dailyDTO.getDescription())
                .difficulty(dailyDTO.getDifficulty())
                .repeatFrequency(dailyDTO.getRepeatFrequency())
                .repeatEvery(dailyDTO.getRepeatEvery())
                .repeatDays(dailyDTO.getRepeatDays())
                .repeatMonthDays(dailyDTO.getRepeatMonthDays())
                .challengeId(dailyDTO.getChallengeId())
                .build();
        daily = dailyRepository.save(daily);

        UserDaily userDaily = UserDaily.builder()
                .user(creator)
                .daily(daily)
                .streak(0L)
                .executionTime(null)
                .build();
        userDailyRepository.save(userDaily);
    }

    @Transactional
    public DailyDTO getUpdateDaily(Long dailyId, String username) {
        User dailyUser = this.userService.getUser(username);
        Daily daily = this.dailyRepository.findById(dailyId).get();
        DailyDTO dailyDTO = new DailyDTO().builder()
                .dailyId(daily.getDailyId())
                .userId(dailyUser.getUserId())
                .title(daily.getTitle())
                .description(daily.getDescription())
                .difficulty(daily.getDifficulty())
                .repeatFrequency(daily.getRepeatFrequency())
                .repeatEvery(daily.getRepeatEvery())
                .repeatDays(daily.getRepeatDays())
                .repeatMonthDays(daily.getRepeatMonthDays())
                .challengeId(daily.getChallengeId())
                .build();

        return dailyDTO;
    }

    @Transactional
    public void updateDaily(DailyDTO dailyDTO, String username) {
        User user = userService.getUser(username);

        Daily daily = dailyRepository.findById(dailyDTO.getDailyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy "));

        daily.setTitle(dailyDTO.getTitle());
        daily.setDescription(dailyDTO.getDescription());
        daily.setDifficulty(dailyDTO.getDifficulty());
        daily.setRepeatFrequency(dailyDTO.getRepeatFrequency());
        daily.setRepeatEvery(dailyDTO.getRepeatEvery());
        daily.setRepeatDays(dailyDTO.getRepeatDays());
        daily.setRepeatMonthDays(dailyDTO.getRepeatMonthDays());
        daily.setChallengeId(dailyDTO.getChallengeId());
        this.dailyRepository.save(daily);
    }
}
