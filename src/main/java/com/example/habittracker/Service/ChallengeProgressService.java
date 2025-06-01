package com.example.habittracker.Service;

import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserChallengeDailyProgress;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeProgressService {
    private final UserDailyRepository userDailyRepository;
    private final UserHabitRepository userHabitRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final DailyHistoryRepository dailyHistoryRepository;
    private final UserChallengeDPRepository userChallengeDPRepository;
    private final UserChallengeRepository userChallengeRepository;

    public ChallengeProgressService(UserDailyRepository userDailyRepository, UserHabitRepository userHabitRepository, HabitHistoryRepository habitHistoryRepository, DailyHistoryRepository dailyHistoryRepository, UserChallengeDPRepository userChallengeDPRepository, UserChallengeRepository userChallengeRepository) {
        this.userDailyRepository = userDailyRepository;
        this.userHabitRepository = userHabitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.dailyHistoryRepository = dailyHistoryRepository;
        this.userChallengeDPRepository = userChallengeDPRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    @Transactional
    public void calculateAndSaveDailyProgress(Long userChallengeId, LocalDate targetDate) {
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu(userChallenge)"));

        // Lấy tất cả Daily Habits của người dùng thuộc challenge này mà HOẠT ĐỘNG
        List<UserDaily> activeUserDailies = userDailyRepository.findByUserAndDailyChallengeAndIsEnabledTrue(userChallenge.getUser(), userChallenge.getChallenge());

        // Lấy tất cả Habits của người dùng thuộc challenge này mà HOẠT ĐỘNG
        List<UserHabit> activeUserHabits = userHabitRepository.findByUserAndHabitChallengeAndIsActiveTrue(userChallenge.getUser(), userChallenge.getChallenge());

        // --- Tính Total Expected Tasks cho ngày targetDate ---
        long expectedTasksForDay = 0L;
        for (UserDaily ud : activeUserDailies) {
            if (enableToday(ud, targetDate)) {
                expectedTasksForDay++;
            }
        }
        for (UserHabit uh : activeUserHabits) {
            expectedTasksForDay++;
        }


        // --- Tính Total Completed Tasks cho ngày targetDate ---
        long completedTasksForDay = 0L;

        completedTasksForDay += dailyHistoryRepository.countByUserDailyInAndDateAndIsCompletedTrue(activeUserDailies, targetDate);

        completedTasksForDay += habitHistoryRepository.countByUserHabitInAndDateAndIsCompletedTrue(activeUserHabits, targetDate);


        // --- Tính completionPercentage cho ngày targetDate ---
        int completionPercentage = 0;
        if (expectedTasksForDay > 0) {
            completionPercentage = (int) Math.round((double) completedTasksForDay / expectedTasksForDay * 100.0);
        } else{
            completionPercentage = 100;
        }

        // --- Lưu hoặc cập nhật UserChallengeDailyProgress ---
        UserChallengeDailyProgress dailyProgress = userChallengeDPRepository
                .findByUserChallengeAndDate(userChallenge, targetDate)
                .orElse(UserChallengeDailyProgress.builder()
                        .userChallenge(userChallenge)
                        .date(targetDate)
                        .build());

        dailyProgress.setCompletionPercentage(completionPercentage);
        userChallengeDPRepository.save(dailyProgress);

        int completionThreshold = 100;
        UserChallengeDailyProgress userChallengeDailyProgress = this.userChallengeDPRepository.findByUserChallengeAndDate(userChallenge,targetDate).get();
        if(userChallengeDailyProgress.getCompletionPercentage() >= completionThreshold){
            if(!userChallenge.getCompletedTasksList().contains(targetDate)){
                userChallenge.getCompletedTasksList().add(targetDate);
                this.userChallengeRepository.save(userChallenge);
            }
        }else {
            if(userChallenge.getCompletedTasksList().contains(targetDate)){
                userChallenge.getCompletedTasksList().remove(targetDate);
                this.userChallengeRepository.save(userChallenge);
            }
        }
    }

    @Transactional
    public void recalculateUserChallengeProgress(UserChallenge userChallenge) {;

        // --- BƯỚC 1: Xác định phạm vi ngày của thử thách của người dùng ---
        LocalDate challengeStartDate = userChallenge.getStartDate();
        LocalDate challengeEndDate = userChallenge.getEndDate();

        // --- BƯỚC 2: Lấy các UserDaily, UserHabit, Todo (active) thuộc thử thách này của người dùng ---
        List<UserDaily> activeUserDailies = userDailyRepository.findByUserAndDailyChallengeAndIsEnabledTrue(userChallenge.getUser(), userChallenge.getChallenge());
        List<UserHabit> activeUserHabits = userHabitRepository.findByUserAndHabitChallengeAndIsActiveTrue(userChallenge.getUser(), userChallenge.getChallenge());

        // --- BƯỚC 3: Tính Total Expected Tasks và completed/skipped cho biểu đồ tròn ---
        long totalExpectedTasks = 0L;
        long completedTasksForChart = 0L;
        long skippedTasksForChart = 0L;

        // Tổng hợp từ Daily Habits
        for (UserDaily ud : activeUserDailies) {
            long expectedDailyTasks = calculateExpectedDailyTasksInPeriod(ud, challengeStartDate, challengeEndDate);
            totalExpectedTasks += expectedDailyTasks;

            // Đếm số lần hoàn thành thực tế cho Daily History trong khoảng thời gian thử thách
            long actualCompletedDailyTasks = dailyHistoryRepository.countByUserDailyAndDateBetweenAndIsCompletedTrue(ud, challengeStartDate, challengeEndDate);
            completedTasksForChart += actualCompletedDailyTasks;
        }

        // Tổng hợp từ Habits
        for (UserHabit uh : activeUserHabits) {
            long expectedHabitTasks = calculateExpectedHabitTasksInPeriod(uh, challengeStartDate, challengeEndDate);
            totalExpectedTasks += expectedHabitTasks;

            // Đếm số lần hoàn thành thực tế cho Habit History trong khoảng thời gian thử thách
            long actualCompletedHabitTasks = habitHistoryRepository.countByUserHabitAndDateBetweenAndIsCompletedTrue(uh, challengeStartDate, challengeEndDate);
            completedTasksForChart += actualCompletedHabitTasks;
        }

        // Tính skippedTasksForChart
        long expectedTasksUpToToday = calculateTotalExpectedTasksUpToDate(userChallenge, LocalDate.now());
        skippedTasksForChart = expectedTasksUpToToday - completedTasksForChart;
        if (skippedTasksForChart < 0) {
            skippedTasksForChart = 0L;
        }


        // --- BƯỚC 4: Cập nhật các trường trong UserChallenge ---
        userChallenge.setTotalExpectedTasks(totalExpectedTasks);
        userChallenge.setTotalCompletedTasks(completedTasksForChart);
        userChallenge.setCompletedTasks(completedTasksForChart);
        userChallenge.setSkippedTasks(skippedTasksForChart);

        // Tính progress tổng
        if (totalExpectedTasks == 0) {
            userChallenge.setProgress(100.0);
        } else {
            double progressValue = Math.round(((double) userChallenge.getTotalCompletedTasks() / totalExpectedTasks * 100.0) * 10) / 10.0;
            userChallenge.setProgress(progressValue);
        }

        userChallengeRepository.save(userChallenge);
    }

    private Long calculateExpectedDailyTasksInPeriod(UserDaily userDaily, LocalDate startDate, LocalDate endDate) {
        Long count = 0L;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (enableToday(userDaily, current)) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }


    private Long calculateExpectedHabitTasksInPeriod(UserHabit userHabit, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private Long calculateTotalExpectedTasksUpToDate(UserChallenge userChallenge, LocalDate upToDate) {
        long expectedTasks = 0L;
        LocalDate challengeStartDate = userChallenge.getStartDate();
        LocalDate actualEndDate = upToDate.isBefore(userChallenge.getEndDate()) ? upToDate : userChallenge.getEndDate();

        if (challengeStartDate.isAfter(actualEndDate)) {
            return 0L;
        }

        List<UserDaily> activeUserDailies = userDailyRepository.findByUserAndDailyChallengeAndIsEnabledTrue(userChallenge.getUser(), userChallenge.getChallenge());
        List<UserHabit> activeUserHabits = userHabitRepository.findByUserAndHabitChallengeAndIsActiveTrue(userChallenge.getUser(), userChallenge.getChallenge());

        for (UserDaily ud : activeUserDailies) {
            expectedTasks += calculateExpectedDailyTasksInPeriod(ud, challengeStartDate, actualEndDate);
        }

        for (UserHabit uh : activeUserHabits) {
            expectedTasks += ChronoUnit.DAYS.between(challengeStartDate, actualEndDate) + 1;
        }
        return expectedTasks;
    }

    @Transactional
    public void updateChallengeStreak(UserChallenge userChallenge) {

        LocalDate today = LocalDate.now();
        int completionThreshold = 100;

        LocalDate yesterday = today.minusDays(1);

        boolean wasCompletedYesterday = false;
        if (!yesterday.isBefore(userChallenge.getStartDate()) && !yesterday.isAfter(userChallenge.getEndDate())) {
            Optional<UserChallengeDailyProgress> yesterdayProgressOpt = userChallengeDPRepository
                    .findByUserChallengeAndDate(userChallenge, yesterday);
            if (yesterdayProgressOpt.isPresent()) {
                wasCompletedYesterday = yesterdayProgressOpt.get().getCompletionPercentage() >= completionThreshold;
            }
        } else if (yesterday.isBefore(userChallenge.getStartDate())) {
            wasCompletedYesterday = true;
        }


        boolean isCompletedToday = false;
        if (!today.isBefore(userChallenge.getStartDate()) && !today.isAfter(userChallenge.getEndDate())) {
            Optional<UserChallengeDailyProgress> todayProgressOpt = userChallengeDPRepository
                    .findByUserChallengeAndDate(userChallenge, today);
            if (todayProgressOpt.isPresent()) {
                isCompletedToday = todayProgressOpt.get().getCompletionPercentage() >= completionThreshold;
            }
        }

        long currentStreak = userChallenge.getStreak();

        if (!today.isBefore(userChallenge.getStartDate()) && !today.isAfter(userChallenge.getEndDate())) {
            if (isCompletedToday) {
                if (wasCompletedYesterday || today.equals(userChallenge.getStartDate())) {
                    currentStreak++;
                } else {
                    currentStreak = 1L;
                }
            } else {
                currentStreak = 0L;
            }
        }

        long bestStreak = userChallenge.getBestStreak();
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }

        userChallenge.setStreak(currentStreak);
        userChallenge.setBestStreak(bestStreak);
        userChallengeRepository.save(userChallenge);
    }


    public boolean enableToday(UserDaily userDaily, LocalDate today) {
        long daysSinceCreation = ChronoUnit.DAYS.between(userDaily.getDaily().getCreateAt(), today);
        switch (userDaily.getRepeatFrequency()) {
            case DAILY:
                return daysSinceCreation % userDaily.getRepeatEvery() == 0;
            case WEEKLY:
                long weeksSinceCreation = daysSinceCreation / 7;
                if (weeksSinceCreation % userDaily.getRepeatEvery() != 0) {
                    return false;
                }
                UserDaily.DayOfWeek todayDayOfWeek = UserDaily.DayOfWeek.valueOf(today.getDayOfWeek().toString());
                return userDaily.getRepeatDays().contains(todayDayOfWeek);
            case MONTHLY:
                long monthsSinceCreation = ChronoUnit.MONTHS.between(userDaily.getDaily().getCreateAt().withDayOfMonth(1), today.withDayOfMonth(1));
                if (monthsSinceCreation % userDaily.getRepeatEvery() != 0) {
                    return false;
                }
                return userDaily.getRepeatMonthDays().contains(today.getDayOfMonth());
            default:
                return false;
        }
    }
}
