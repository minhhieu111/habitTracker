package com.example.habittracker.Service;

import com.example.habittracker.DTO.ChallengeDTO;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.ChallengeRepository;
import com.example.habittracker.Repository.UserChallengeDPRepository;
import com.example.habittracker.Repository.UserChallengeRepository;
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

    public ChallengeService(ChallengeRepository challengeRepository, UserService userService, HabitService habitService, DailyService dailyService, UserChallengeRepository userChallengeRepository, UserChallengeDPRepository userChallengeDPRepository) {
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.userChallengeRepository = userChallengeRepository;
        this.userChallengeDPRepository = userChallengeDPRepository;
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
                .completedTasks(0L)
                .skippedTasks(0L)
                .build();
        userChallengeRepository.save(userChallenge);
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
