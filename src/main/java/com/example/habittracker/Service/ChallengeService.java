package com.example.habittracker.Service;

import com.example.habittracker.DTO.ChallengeDTO;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserService userService;
    private final HabitService habitService;
    private final DailyService dailyService;
    private final UserChallengeRepository userChallengeRepository;
    private final UserChallengeDPRepository userChallengeDPRepository;
    private final UserDailyRepository userDailyRepository;
    private final UserHabitRepository userHabitRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final DailyHistoryRepository dailyHistoryRepository;
    private final UserChallengeDailyProgressRepository userChallengeDailyProgressRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserService userService, HabitService habitService, DailyService dailyService, UserChallengeRepository userChallengeRepository, UserChallengeDPRepository userChallengeDPRepository, UserDailyRepository userDailyRepository, UserHabitRepository userHabitRepository, HabitHistoryRepository habitHistoryRepository, DailyHistoryRepository dailyHistoryRepository, UserChallengeDailyProgressRepository userChallengeDailyProgressRepository) {
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.userChallengeRepository = userChallengeRepository;
        this.userChallengeDPRepository = userChallengeDPRepository;
        this.userDailyRepository = userDailyRepository;
        this.userHabitRepository = userHabitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.dailyHistoryRepository = dailyHistoryRepository;
        this.userChallengeDailyProgressRepository = userChallengeDailyProgressRepository;
    }

    public List<UserChallenge> getChallenges(Long userId) {
        return this.challengeRepository.findChallengeByUsers_Username(userId).get();
    }

    public ChallengeDTO getChallengeById(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        UserChallenge userChallenge = userChallengeRepository.findByChallenge(challenge)
                .orElseThrow(() -> new RuntimeException("UserChallenge not found"));

        List<HabitDTO> habits = habitService.getHabitsByChallengeId(challengeId);
        List<DailyDTO> dailies = dailyService.getDailiesByChallengeId(challengeId);

        return ChallengeDTO.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .endDate(userChallenge.getEndDate())
                .day(challenge.getDay())
                .isPublic(challenge.getIsPublic())
                .habits(habits)
                .dailies(dailies)
                .build();
    }

    @Transactional
    public void createChallenge(ChallengeDTO challengeDTO, User creator) {

        if (challengeDTO.getTitle() == null || challengeDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề thử thách không được để trống!");
        }
        if (challengeDTO.getEndDate() == null || challengeDTO.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày kết thúc không hợp lệ!");
        }

        LocalDate startDate = LocalDate.now();

        if (challengeDTO.getDay() < 5) {
            throw new RuntimeException("Thời gian thực hiện phải ít nhất 5 ngày!");
        }

        Challenge challenge = Challenge.builder()
                .creatorId(creator.getUserId())
                .title(challengeDTO.getTitle())
                .description(challengeDTO.getDescription())
                .day(challengeDTO.getDay())
                .isPublic(Challenge.Visibility.PRIVATE)
                .participantCount(1L)
                .build();
        challenge = challengeRepository.save(challenge);


        if (challengeDTO.getHabits() != null) {
            for (HabitDTO habitDTO : challengeDTO.getHabits()) {
                habitDTO.setChallengeId(challenge.getChallengeId());
                habitService.save(habitDTO, creator.getUserName());
            }
        }

        if (challengeDTO.getDailies() != null) {
            for (DailyDTO dailyDTO : challengeDTO.getDailies()) {
                dailyDTO.setChallengeId(challenge.getChallengeId());
                dailyService.createDaily(dailyDTO, creator.getUserName());
            }
        }

        UserChallenge userChallenge = UserChallenge.builder()
                .user(creator)
                .challenge(challenge)
                .progress(0.0)
                .startDate(startDate)
                .endDate(challengeDTO.getEndDate())
                .status(UserChallenge.Status.ACTIVE)
                .streak(0L)
                .bestStreak(0L)
                .daysSinceStart(0L)
                .totalCompletedTasks(0L)
                .totalExpectedTasks(0L)
                .completedTasks(0L)
                .skippedTasks(0L)
                .build();
        userChallengeRepository.save(userChallenge);

        calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),startDate);
        recalculateUserChallengeProgress(userChallenge);
    }

    @Transactional
    public void updateChallenge(ChallengeDTO challengeDTO, User creator) {
        Challenge challenge = challengeRepository.findById(challengeDTO.getChallengeId())
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        UserChallenge userChallenge = userChallengeRepository.findByChallenge(challenge)
                .orElseThrow(() -> new RuntimeException("UserChallenge not found"));

        if (challengeDTO.getTitle() == null || challengeDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề thử thách không được để trống!");
        }
        if (challengeDTO.getEndDate() == null || challengeDTO.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày kết thúc không hợp lệ!");
        }

        if (challengeDTO.getDay() < 5) {
            throw new RuntimeException("Thời gian thực hiện phải ít nhất 5 ngày!");
        }

        // Cập nhật Challenge
        challenge.setTitle(challengeDTO.getTitle());
        challenge.setDescription(challengeDTO.getDescription());
        challenge.setDay(challengeDTO.getDay());
        challengeRepository.save(challenge);

        // Cập nhật UserChallenge
        userChallenge.setEndDate(challengeDTO.getEndDate());
        userChallengeRepository.save(userChallenge);

        calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),LocalDate.now());
        recalculateUserChallengeProgress(userChallenge);

        // Xử lý Habits
        List<HabitDTO> existingHabits = habitService.getHabitsByChallengeId(challenge.getChallengeId());
        List<Long> newHabitIds = challengeDTO.getHabits().stream()
                .map(HabitDTO::getHabitId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // Xóa các Habit không còn trong danh sách mới
        for (HabitDTO existingHabit : existingHabits) {
            if (!newHabitIds.contains(existingHabit.getHabitId())) {
                habitService.unlinkHabitFromChallenge(existingHabit.getHabitId());
            }
        }

        // Thêm các Habit mới
        for (HabitDTO habitDTO : challengeDTO.getHabits()) {
            if (habitDTO.getHabitId() == null) {
                habitDTO.setChallengeId(challenge.getChallengeId());
                habitService.save(habitDTO, creator.getUserName());
            }
        }

        // Xử lý Dailies
        List<DailyDTO> existingDailies = dailyService.getDailiesByChallengeId(challenge.getChallengeId());
        List<Long> newDailyIds = challengeDTO.getDailies().stream()
                .map(DailyDTO::getDailyId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // Xóa các Daily không còn trong danh sách mới
        for (DailyDTO existingDaily : existingDailies) {
            if (!newDailyIds.contains(existingDaily.getDailyId())) {
                dailyService.unlinkDailyFromChallenge(existingDaily.getDailyId());
            }
        }

        // Thêm các Daily mới
        for (DailyDTO dailyDTO : challengeDTO.getDailies()) {
            if (dailyDTO.getDailyId() == null) {
                dailyDTO.setChallengeId(challenge.getChallengeId());
                dailyService.createDaily(dailyDTO, creator.getUserName());
            }
        }
    }

    @Transactional
    public void deleteChallenge(Long challengeId, User creator) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()->new RuntimeException("Không tìm thấy thử thách! Xóa thất bại"));
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(creator,challenge).orElseThrow(()->new RuntimeException("Lỗi khi tìm dữ liệu! Xóa thất bại"));

        List<Habit> habits = this.challengeRepository.findHabitsByChallengeId(challenge.getChallengeId());
        habits.forEach(habit -> {this.habitService.unlinkHabitFromChallenge(habit.getHabitId());});

        List<Daily> dailies = this.challengeRepository.findDailiesByChallengeId(challenge.getChallengeId());
        dailies.forEach(daily -> {this.dailyService.unlinkDailyFromChallenge(daily.getDailyId());});

        this.userChallengeDPRepository.deleteAllByUserChallenge(userChallenge);
        this.userChallengeRepository.delete(userChallenge);
        this.challengeRepository.delete(challenge);
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
            if (dailyService.enableToday(ud, targetDate)) {
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
        UserChallengeDailyProgress dailyProgress = userChallengeDailyProgressRepository
                .findByUserChallengeAndDate(userChallenge, targetDate)
                .orElse(UserChallengeDailyProgress.builder()
                        .userChallenge(userChallenge)
                        .date(targetDate)
                        .build());

        dailyProgress.setCompletionPercentage(completionPercentage);
        userChallengeDailyProgressRepository.save(dailyProgress);
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
            double progressValue = (double) userChallenge.getTotalCompletedTasks() / totalExpectedTasks * 100.0;
            userChallenge.setProgress(progressValue);
        }

        userChallengeRepository.save(userChallenge);
    }

    private Long calculateExpectedDailyTasksInPeriod(UserDaily userDaily, LocalDate startDate, LocalDate endDate) {
        Long count = 0L;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (dailyService.enableToday(userDaily, current)) {
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


    @Transactional(readOnly = true)
    public List<ChallengeDTO> getCompletedChallenges(String username) {
        User user = userService.getUser(username);
        return userChallengeRepository.findByUser(user).stream()
                .filter(uc -> uc.getStatus() == UserChallenge.Status.COMPLETE && uc.getChallenge().getIsPublic() == Challenge.Visibility.PUBLIC)
                .map(uc -> {
                    Challenge challenge = uc.getChallenge();
                    return ChallengeDTO.builder()
                            .challengeId(challenge.getChallengeId())
                            .title(challenge.getTitle())
                            .description(challenge.getDescription())
                            .endDate(uc.getEndDate())
                            .day(challenge.getDay())
                            .isPublic(challenge.getIsPublic())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void completeChallenge(Long challengeId, String username) {
        User user = userService.getUser(username);
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challengeRepository.findById(challengeId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách!")))
                .orElseThrow(() -> new RuntimeException("Bạn không tham gia thử thách này!"));

        Long dateComplete = ChronoUnit.DAYS.between(userChallenge.getStartDate(), LocalDate.now());

        if (userChallenge.getStatus() == UserChallenge.Status.ACTIVE && userChallenge.getChallenge().getDay() >= dateComplete) {
            userChallenge.setStatus(UserChallenge.Status.COMPLETE);
            Challenge challenge = userChallenge.getChallenge();
            challenge.setIsPublic(Challenge.Visibility.PUBLIC);
            userChallengeRepository.save(userChallenge);
            challengeRepository.save(challenge);
        } else {
            throw new RuntimeException("Thử thách chưa hoàn thành hoặc không thể chia sẻ!");
        }
    }

}
