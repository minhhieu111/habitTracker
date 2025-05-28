package com.example.habittracker.Service;

import com.example.habittracker.DTO.ChallengeDTO;
import com.example.habittracker.DTO.DailyDTO;
import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.Challenge;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Repository.ChallengeRepository;
import com.example.habittracker.Repository.UserChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final UserService userService;
    private final HabitService habitService;
    private final DailyService dailyService;
    private final UserChallengeRepository userChallengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserService userService, HabitService habitService, DailyService dailyService, UserChallengeRepository userChallengeRepository) {
        this.challengeRepository = challengeRepository;
        this.userService = userService;
        this.habitService = habitService;
        this.dailyService = dailyService;
        this.userChallengeRepository = userChallengeRepository;
    }

    public List<UserChallenge> getChallenges(Long userId) {
        return this.challengeRepository.findChallengeByUsers_Username(userId).get();
    }

    @Transactional
    public void createChallenge(ChallengeDTO challengeDTO, String username) {
        User creator = userService.getUser(username);

        // Validate input
        if (challengeDTO.getTitle() == null || challengeDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề thử thách không được để trống!");
        }
        if (challengeDTO.getEndDate() == null || challengeDTO.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Ngày kết thúc không hợp lệ!");
        }

        // Tính số ngày thực hiện
        LocalDate startDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(startDate, challengeDTO.getEndDate());
        if (days < 10) {
            throw new RuntimeException("Thời gian thực hiện phải ít nhất 10 ngày!");
        }

        // Tạo Challenge
        Challenge challenge = Challenge.builder()
                .title(challengeDTO.getTitle())
                .description(challengeDTO.getDescription())
                .day(days)
                .isPublic(Challenge.Visibility.PRIVATE)
                .participantCount(1L) // Người tạo là người tham gia đầu tiên
                .build();
        challenge = challengeRepository.save(challenge);

        // Tạo các Habit liên quan
        if (challengeDTO.getHabits() != null) {
            for (HabitDTO habitDTO : challengeDTO.getHabits()) {
                habitDTO.setChallengeId(challenge.getChallengeId());
                habitService.save(habitDTO, username);
            }
        }

        // Tạo các Daily liên quan
        if (challengeDTO.getDailies() != null) {
            for (DailyDTO dailyDTO : challengeDTO.getDailies()) {
                dailyDTO.setChallengeId(challenge.getChallengeId());
                dailyService.createDaily(dailyDTO, username);
            }
        }

        // Tạo UserChallenge cho người tạo
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

}
