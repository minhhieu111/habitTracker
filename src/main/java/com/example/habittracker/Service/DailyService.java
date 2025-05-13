package com.example.habittracker.Service;

import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.DailyHistoryRepository;
import com.example.habittracker.Repository.DailyRepository;
import com.example.habittracker.Repository.UserDailyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DailyService {
    private final DailyRepository dailyRepository;
    private final UserService userService;
    private final UserDailyRepository userDailyRepository;
    private final DailyHistoryRepository dailyHistoryRepository;
    public DailyService(DailyRepository dailyRepository, UserService userService, UserDailyRepository userDailyRepository, DailyHistoryRepository dailyHistoryRepository) {
        this.dailyRepository = dailyRepository;
        this.userService = userService;
        this.userDailyRepository = userDailyRepository;
        this.dailyHistoryRepository = dailyHistoryRepository;
    }

    public List<UserDaily> getUserDaily(User user){
        List<UserDaily>userDailies = this.dailyRepository.findUserDailiesByUserId(user.getUserId()).get();
        LocalDate today = LocalDate.now();

        for(UserDaily userDaily : userDailies){
            userDaily.setEnabled(enableToday(userDaily.getDaily(), today));
            this.userDailyRepository.save(userDaily);
        }
        return userDailies;
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
                .createAt(LocalDate.now())
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

    @Transactional
    public void deleteDaily(Long dailyId, String username) {
        User user = userService.getUser(username);
        Daily daily = this.dailyRepository.findById(dailyId).get();
        UserDaily userDaily = this.userDailyRepository.findByUserAndDaily(user,daily);
        if(daily == null && userDaily == null){
            throw new RuntimeException("Có lỗi xảy ra khi xóa! Không tìm thấy thói quen để xóa");
        }
        List<DailyHistory> dailyHistories = this.dailyHistoryRepository.findAllByUserDaily(userDaily);

        this.dailyHistoryRepository.deleteAll(dailyHistories);
        this.dailyRepository.delete(daily);
        this.userDailyRepository.delete(userDaily);
    }


    private boolean enableToday(Daily daily, LocalDate today) {

        long daysSinceCreation = ChronoUnit.DAYS.between(daily.getCreateAt(), today);
        switch (daily.getRepeatFrequency()) {
            case DAILY:
                return daysSinceCreation % daily.getRepeatEvery() == 0;
            case WEEKLY:
                long weeksSinceCreation = daysSinceCreation / 7;
                if (weeksSinceCreation % daily.getRepeatEvery() != 0) {
                    return false;
                }
                Daily.DayOfWeek todayDayOfWeek = Daily.DayOfWeek.valueOf(today.getDayOfWeek().toString());
                return daily.getRepeatDays().contains(todayDayOfWeek);
            case MONTHLY:
                long monthsSinceCreation = ChronoUnit.MONTHS.between(daily.getCreateAt().withDayOfMonth(1), today.withDayOfMonth(1));
                if (monthsSinceCreation % daily.getRepeatEvery() != 0) {
                    return false;
                }
                return daily.getRepeatMonthDays().contains(today.getDayOfMonth());
            default:
                return false;
        }
    }

    @Transactional
    public DailyDTO dailyTaskUpdate(String username, Long dailyId, String status) {
        Daily daily = this.dailyRepository.findById(dailyId).get();
        User user = this.userService.getUser(username);
        UserDaily userDaily = this.userDailyRepository.findByUserAndDaily(user,daily);
        DailyDTO dailyDTO = new DailyDTO();
        LocalDate today = LocalDate.now();

        DailyHistory dailyHistory = this.dailyHistoryRepository.findDailyHistory(userDaily,today).orElseGet(()->
                new DailyHistory().builder()
                        .userDaily(userDaily)
                        .date(today)
                        .streak(userDaily.getStreak())
                        .isCompleted(false)
                        .build()
        );

        if(status.equals("checked")){
            userDaily.setCompleted(true);
            dailyHistory.setCompleted(true);
            userDaily.setStreak(userDaily.getStreak() + 1);
        } else if (status.equals("unchecked")) {
            userDaily.setCompleted(false);
            dailyHistory.setCompleted(false);
            userDaily.setStreak(userDaily.getStreak() - 1);
        }

        this.userDailyRepository.save(userDaily);
        dailyHistory.setStreak(userDaily.getStreak());
        this.dailyHistoryRepository.save(dailyHistory);

//        streakCalculator(userDaily,today);

        dailyDTO.setStreak(userDaily.getStreak());
        dailyDTO.setCompleted(userDaily.isCompleted());

        return dailyDTO;
    }

    @Transactional
    public void streakCalculator(UserDaily userDaily,LocalDate today){
        DailyHistory newDailyHistory = this.dailyHistoryRepository.findDailyHistory(userDaily,today).get();

        if(newDailyHistory.isCompleted()){
            userDaily.setStreak(userDaily.getStreak() + 1);
        }else{
            userDaily.setStreak(userDaily.getStreak() - 1);
        }
        this.userDailyRepository.save(userDaily);
        newDailyHistory.setStreak(userDaily.getStreak());
        this.dailyHistoryRepository.save(newDailyHistory);
    }

    @Transactional
    public void resetDaily(){
        List<UserDaily> userDailies = this.userDailyRepository.findAll();
        LocalDate today = LocalDate.now();
        for(UserDaily userDaily : userDailies){
            if(enableToday(userDaily.getDaily(),today)){
                DailyHistory lastDailyHistory = findLastEnableHistory(userDaily,today);
                DailyHistory dailyHistory = this.dailyHistoryRepository.findDailyHistory(userDaily,today).orElseGet(()->
                        new DailyHistory().builder()
                                .userDaily(userDaily)
                                .date(today)
                                .streak(0L)
                                .isCompleted(false)
                                .build()
                );
                if(dailyHistory != null && lastDailyHistory != null){
                    if(lastDailyHistory.isCompleted() && dailyHistory.isCompleted()){
                        userDaily.setStreak(userDaily.getStreak());
                    } else if (lastDailyHistory.isCompleted() && !dailyHistory.isCompleted()) {
                        userDaily.setStreak(0L);
                    }else if(!lastDailyHistory.isCompleted() && dailyHistory.isCompleted()){
                        userDaily.setStreak(1L);
                    }else if(!lastDailyHistory.isCompleted() && !dailyHistory.isCompleted()){
                        userDaily.setStreak(0L);
                    }
                }else if(dailyHistory != null){
                    if(dailyHistory.isCompleted()){
                        userDaily.setStreak(userDaily.getStreak());
                    }else{
                        userDaily.setStreak(0L);
                    }
                }
                userDaily.setCompleted(false);

                this.userDailyRepository.save(userDaily);
                this.dailyHistoryRepository.save(dailyHistory);
            }
        }
    }

    public DailyHistory findLastEnableHistory(UserDaily userDaily, LocalDate today){
        List<DailyHistory> dailyHistories = this.dailyHistoryRepository.findAllByUserDailyOrderByDateDesc(userDaily);
        for(DailyHistory dailyHistory : dailyHistories){
            if(dailyHistory.getDate().isBefore(today) && enableToday(userDaily.getDaily(),dailyHistory.getDate())){
                return dailyHistory;
            }
        }
        return null;
    }
}
