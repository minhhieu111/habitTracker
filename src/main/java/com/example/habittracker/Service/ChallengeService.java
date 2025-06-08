package com.example.habittracker.Service;

import com.example.habittracker.DTO.ChallengeDTO;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.DailyProgressDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserService userService;
    private final HabitService habitService;
    private final DailyService dailyService;
    private final UserChallengeRepository userChallengeRepository;
    private final UserChallengeDPRepository userChallengeDPRepository;
    private final ChallengeProgressService challengeProgressService;
    private final UserHabitRepository userHabitRepository;
    private final UserDailyRepository userDailyRepository;
    private final HabitHistoryRepository habitHistoryRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserService userService, HabitService habitService, DailyService dailyService, UserChallengeRepository userChallengeRepository, UserChallengeDPRepository userChallengeDPRepository, ChallengeProgressService challengeProgressService, UserHabitRepository userHabitRepository, UserDailyRepository userDailyRepository, HabitHistoryRepository habitHistoryRepository) {
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.userChallengeRepository = userChallengeRepository;
        this.userChallengeDPRepository = userChallengeDPRepository;
        this.challengeProgressService = challengeProgressService;
        this.userHabitRepository = userHabitRepository;
        this.userDailyRepository = userDailyRepository;
        this.habitHistoryRepository = habitHistoryRepository;
    }
    @Transactional
    public List<UserChallenge> getChallenges(Long userId) {
        return this.challengeRepository.findUnCompleteChallengeByUsers_Username(userId).get();
    }

    @Transactional
    public List<UserChallenge> getUnCompleteChallenges(Long userId) {
        return this.challengeRepository.findChallengeByUsers_Username(userId).get();
    }

    @Transactional
    public List<UserChallenge> getChallengesOwner(Long userId){
        return this.challengeRepository.findUserChallengeOwner(userId).get();
    }

    @Transactional
    public Challenge getChallengeById(Long challengeId) {
        return this.challengeRepository.findById(challengeId).get();
    }

    @Transactional
    public List<UserChallenge> getValidChallenges(Long userId) {
        List<UserChallenge> userChallenges = this.challengeRepository.findChallengeByUsers_Username(userId).get();
        LocalDate today = LocalDate.now();
        return userChallenges.stream()
                .filter(uc->{
                        if (uc.getStatus() == UserChallenge.Status.ACTIVE) {
                            return !today.isAfter(uc.getEndDate());
                        }else if (uc.getStatus() == UserChallenge.Status.COMPLETE) {
                            if (uc.getProgress() != null && uc.getProgress() >= 100.0) {
                                return !today.isAfter(uc.getEndDate());
                            }
                        }return false;
                        }
                     ).collect(Collectors.toList());
    }
    @Transactional
    public List<UserChallenge> getCompletedChallengesForNotification(User user) {
        return userChallengeRepository.findByUserAndStatusAndIsNotificationShownFalse(user, UserChallenge.Status.COMPLETE);
    }

    @Transactional
    public ChallengeDTO getUserChallengeDetail(User user, Long challengeId) {
        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Challenge với ID: " + challengeId));

        UserChallenge userChallenge = this.userChallengeRepository.findByUserAndChallenge(user, challenge)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy UserChallenge của người dùng này cho Challenge đó."));

        List<DailyProgressDTO> dailyProgressDTOs = userChallenge.getDailyProgresses()
                .stream()
                .map(dp -> DailyProgressDTO.builder()
                        .date(dp.getDate())
                        .completionPercentage(dp.getCompletionPercentage())
                        .build()).collect(Collectors.toList());

        return ChallengeDTO.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .progress(userChallenge.getProgress())
                .bestStreak(userChallenge.getBestStreak())
                .totalCompletedTasks(userChallenge.getTotalCompletedTasks())
                .completedTasks(userChallenge.getCompletedTasks())
                .skippedTasks(userChallenge.getSkippedTasks())
                .completedTasksList(userChallenge.getCompletedTasksList())
                .dailyProgresses(dailyProgressDTOs)
                .completedTasksList(userChallenge.getCompletedTasksList())
                .status(userChallenge.getStatus())
                .build();
    }
    @Transactional
    public ChallengeDTO getChallengeDTOById(Long challengeId,User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách"));
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user,challenge).get();

        List<HabitDTO> habits = habitService.getHabitsByUser_ChallengeId(challengeId);
        List<DailyDTO> dailies = dailyService.getDailiesByChallengeId(challengeId);

        return ChallengeDTO.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .challengeParticipant(challenge.getParticipantCount())
                .day(challenge.getDay())
                .endDate(userChallenge.getEndDate() != null ? userChallenge.getEndDate() : null)
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
                .daysSinceStart(ChronoUnit.DAYS.between(startDate, LocalDate.now()))
                .totalCompletedTasks(0L)
                .totalExpectedTasks(0L)
                .completedTasks(0L)
                .skippedTasks(0L)
                .completedTasksList(new ArrayList<>())
                .build();
        userChallengeRepository.save(userChallenge);

        challengeProgressService.calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),startDate);
        challengeProgressService.recalculateUserChallengeProgress(userChallenge);
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

        challengeProgressService.calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),LocalDate.now());
        challengeProgressService.recalculateUserChallengeProgress(userChallenge);

        // Xử lý Habits
        List<HabitDTO> existingHabits = habitService.getHabitsByUser_ChallengeId(challenge.getChallengeId());
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

        if(challenge.getIsPublic() == Challenge.Visibility.PRIVATE){
            List<Habit> habits = this.challengeRepository.findHabitsByChallengeId(challenge.getChallengeId());
            habits.forEach(habit -> {this.habitService.unlinkHabitFromChallenge(habit.getHabitId());});

            List<Daily> dailies = this.challengeRepository.findDailiesByChallengeId(challenge.getChallengeId());
            dailies.forEach(daily -> {this.dailyService.unlinkDailyFromChallenge(daily.getDailyId());});

            this.userChallengeDPRepository.deleteAllByUserChallenge(userChallenge);
            this.userChallengeRepository.delete(userChallenge);
            this.challengeRepository.delete(challenge);
        }else{
            this.userChallengeDPRepository.deleteAllByUserChallenge(userChallenge);
            this.userChallengeRepository.delete(userChallenge);
        }

    }


    //community----------------------------------------------------------------------------------------------------------
    @Transactional
    public List<UserChallenge> getSharedChallenge(){
        return this.userChallengeRepository.findByChallengePublic();
    }

    @Transactional
    public List<UserChallenge> getUserCompleteChallenge(User user){
        return this.userChallengeRepository.findByUserAndCompleted(user);
    }

    @Transactional
    public void shareChallenge(User user, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thử thách"));

        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge)
                .orElseThrow(() -> new RuntimeException("Lấy dữ liệu thử thách thất bại"));

        if (!challenge.getCreatorId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền chia sẻ thử thách này");
        }
        if (userChallenge.getStatus() != UserChallenge.Status.COMPLETE) {
            throw new RuntimeException("Thử Thách cần hoàn thành trước khi chia sẻ");
        }

        challenge.setIsPublic(Challenge.Visibility.PENDING);
        challengeRepository.save(challenge);
    }

    @Transactional
    public void joinChallenge(User user, Challenge challenge) {
        if (challenge.getIsPublic() != Challenge.Visibility.PUBLIC) {
            throw new RuntimeException("Thử thách chưa được chia sẻ");
        }

        // Kiểm tra xem người dùng đã tham gia chưa
        Optional<UserChallenge> existingUserChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge);
        if (existingUserChallenge.isPresent()) {
            throw new RuntimeException("Bạn đã tham gia thử thách này rồi!");
        }

        // Tạo UserChallenge mới
        UserChallenge userChallenge = UserChallenge.builder()
                .user(user)
                .challenge(challenge)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(challenge.getDay()))
                .status(UserChallenge.Status.ACTIVE)
                .streak(0L)
                .bestStreak(0L)
                .progress(0.0)
                .totalCompletedTasks(0L)
                .daysSinceStart(0L)
                .totalExpectedTasks(0L)
                .completedTasks(0L)
                .skippedTasks(0L)
                .completedTasksList(new ArrayList<>())
                .build();
        userChallengeRepository.save(userChallenge);

        // Sao chép danh sách Habit vào UserHabit
        for (Habit habit : challenge.getHabits()) {
            UserHabit userHabit = UserHabit.builder()
                    .user(user)
                    .habit(habit)
                    .currentCount(0L)
                    .positiveCount(0L)
                    .negativeCount(0L)
                    .targetCount(habit.getTargetCount())
                    .unavailable(false)
                    .difficulty(habit.getDifficulty())
                    .build();
            if(habit.getType() == Habit.HabitType.NEGATIVE){
                userHabit.setCompleted(true);
            }else{
                userHabit.setCompleted(false);
            }
            userHabit.setInChallenge(true);
            userHabitRepository.save(userHabit);
            if (habit.getType() == Habit.HabitType.NEGATIVE) {
                HabitHistory initialHistory = HabitHistory.builder()
                        .userHabit(userHabit)
                        .date(LocalDate.now())
                        .isCompleted(true)
                        .positiveCount(0L)
                        .negativeCount(0L)
                        .coinEarned(0L)
                        .build();
                this.habitHistoryRepository.save(initialHistory);
            };
        }

        // Sao chép danh sách Daily vào UserDaily
        for (Daily daily : challenge.getDailies()) {
            UserDaily userDaily = UserDaily.builder()
                    .user(user)
                    .daily(daily)
                    .streak(0L)
                    .executionTime(null)
                    .isCompleted(false)
                    .isEnabled(true)
                    .difficulty(daily.getDifficulty())
                    .repeatFrequency(daily.getRepeatFrequency())
                    .repeatEvery(daily.getRepeatEvery())
                    .build();
            if(daily.getRepeatFrequency() == Daily.RepeatFrequency.WEEKLY){
//                userDaily.setRepeatDays(new HashSet<>(Arrays.asList(UserDaily.DayOfWeek.values())));
                userDaily.setRepeatDays(new HashSet<>(daily.getUserDailies().get(0).getRepeatDays()));
            } else if (daily.getRepeatFrequency() == Daily.RepeatFrequency.MONTHLY) {
//                userDaily.setRepeatMonthDays(new HashSet<>(Arrays.asList(1, 2, 3)));
                userDaily.setRepeatMonthDays(new HashSet<>(daily.getUserDailies().get(0).getRepeatMonthDays()));
            }
            userDaily.setInChallenge(true);

            userDailyRepository.save(userDaily);
        }

        // Cập nhật participantCount
        challenge.setParticipantCount(challenge.getParticipantCount() + 1);
        challengeRepository.save(challenge);
    }

    @Transactional
    public void CalChallengeProgressEndDay(){
        LocalDate today = LocalDate.now();
        List<UserChallenge> userChallenge = this.userChallengeRepository.findAll();

        List<UserChallenge> activeUserChallenge = userChallenge.stream()
                .filter(uc->uc.getStatus() == UserChallenge.Status.ACTIVE)
                .filter(uc -> !today.isAfter(uc.getEndDate()) && !today.isBefore(uc.getStartDate()) )
                .collect(Collectors.toList());
        for(UserChallenge uc : activeUserChallenge){
            this.challengeProgressService.calculateAndSaveDailyProgress(uc.getUserChallengeId(),LocalDate.now());
            this.challengeProgressService.recalculateUserChallengeProgress(uc);
            this.challengeProgressService.updateChallengeStreak(uc,true);
        }
    }
    @Transactional
    public void checkChallengeCompleted(){
        List<UserChallenge> activeChallenges = userChallengeRepository.findByStatus(UserChallenge.Status.ACTIVE);
        activeChallenges.forEach(challengeProgressService::checkAndCompleteChallenge);
    }
}
