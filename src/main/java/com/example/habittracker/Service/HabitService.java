package com.example.habittracker.Service;

import com.example.habittracker.DTO.HabitDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final UserService userService;
    private final UserHabitRepository userHabitRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeProgressService challengeProgressService;

    public HabitService(HabitRepository habitRepository, UserService userService, UserHabitRepository userHabitRepository, HabitHistoryRepository habitHistoryRepository, ChallengeRepository challengeRepository, UserChallengeRepository userChallengeRepository, ChallengeProgressService challengeProgressService) {
        this.habitRepository = habitRepository;
        this.userService = userService;
        this.userHabitRepository = userHabitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.challengeProgressService = challengeProgressService;
    }

    @Transactional
    public List<UserHabit> getUserHabits(User user) {
        return this.userHabitRepository.findHabitsForUser(user.getUserId()).get();
    }

    @Transactional
    public Habit getHabit(Long habitId) {
        return this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Không tìm thấy thói quen!"));
    }

    @Transactional
    public void save(HabitDTO habitDTO, String username) {
        User user = this.userService.getUser(username);

        if(habitDTO.getTitle().equals("")){
            throw new RuntimeException("Tạo Thói quen thất Bại! Tiêu đề không được để trống");
        }
        if(habitDTO.getTargetCount() < 1 || habitDTO.getTargetCount() == null){
            throw new RuntimeException("Tạo Thói quen thất Bại! Mục tiêu không được để trống và mục tiêu phải lớn hơn 1");
        }
        Challenge challenge = this.challengeRepository.findById(habitDTO.getChallengeId()).get();

        Habit createHabit = Habit.builder()
                .title(habitDTO.getTitle())
                .description(habitDTO.getDescription())
                .type(habitDTO.getType())
                .difficulty(habitDTO.getDifficulty())
                .targetCount(habitDTO.getTargetCount())
                .challenge(challenge)
                .build();
        this.habitRepository.save(createHabit);

        UserHabit userHabit = new UserHabit();
        userHabit.setUser(user);
        userHabit.setHabit(createHabit);
        userHabit.setTargetCount(createHabit.getTargetCount());
        userHabit.setCurrentCount(0L);
        userHabit.setDifficulty(createHabit.getDifficulty());
        this.userHabitRepository.save(userHabit);
    }

    @Transactional
    public HabitDTO getUpdateHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Lỗi khi chỉnh sửa thói quen!"));
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi lấy dữ liệu chỉnh sửa!"));
        HabitDTO habitDTO = new HabitDTO().builder()
                .title(habit.getTitle())
                .description(habit.getDescription())
                .type(habit.getType())
                .difficulty(userHabit.getDifficulty())
                .targetCount(userHabit.getTargetCount())
                .challengeId(habit.getChallenge() != null ? habit.getChallenge().getChallengeId() : null)
                .build();
        return habitDTO;
    }

    @Transactional
    public void updateHabit(HabitDTO habitDTO, String username) {
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitDTO.getHabitId());

        if(habitDTO.getTitle().equals("")){
            throw new RuntimeException("Tạo Thói quen thất Bại! Tiêu đề không được để trống");
        }
        if(habitDTO.getTargetCount() < 1 || habitDTO.getTargetCount() == null){
            throw new RuntimeException("Tạo Thói quen thất Bại! Mục tiêu không được để trống và mục tiêu phải lớn hơn 1");
        }
        Challenge challenge = this.challengeRepository.findById(habitDTO.getChallengeId()).get();

        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setType(habitDTO.getType());
        habit.setChallenge(challenge);
        this.habitRepository.save(habit);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi khi lưu dữ liệu chỉnh sửa!"));
        userHabit.setTargetCount(habitDTO.getTargetCount());
        userHabit.setDifficulty(habitDTO.getDifficulty());
        this.userHabitRepository.save(userHabit);
    }

    @Transactional
    public void deleteHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitId);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi xảy ra khi xóa thói quen!"));
        List<HabitHistory> habitHistories = this.habitHistoryRepository.findAllByUserHabit(userHabit);
        this.habitHistoryRepository.deleteAll(habitHistories);
        this.userHabitRepository.delete(userHabit);
        this.habitRepository.delete(habit);
    }

    @Transactional
    public HabitDTO updateHabitCount(Long habitId, String username, String type){
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitId);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi xảy ra khi xóa thói quen!"));

        String actionType = type.toUpperCase();

        if(actionType.equals("POSITIVE")){
            userHabit.setPositiveCount(userHabit.getPositiveCount()+1);
        }else if(actionType.equals("NEGATIVE")){
            userHabit.setNegativeCount(userHabit.getNegativeCount()+1);
        }else{
            throw new RuntimeException("Lỗi xảy ra khi cập nhật!");
        }

        this.userHabitRepository.save(userHabit);
        LocalDate today = LocalDate.now();

        HabitHistory habitHistory = this.habitHistoryRepository.findByUserHabitAndDate(userHabit,today)
                .map(habitHis->{
                    habitHis.setPositiveCount(userHabit.getPositiveCount());
                    habitHis.setNegativeCount(userHabit.getNegativeCount());
                    return habitHis;
                })
                .orElseGet(()->new HabitHistory().builder()
                        .userHabit(userHabit)
                        .negativeCount(userHabit.getNegativeCount())
                        .positiveCount(userHabit.getPositiveCount())
                        .date(today)
                        .build()
        );

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setPositiveCount(userHabit.getPositiveCount());
        habitDTO.setNegativeCount(userHabit.getNegativeCount());

        if((habit.getType().name().equals("BOTH") && userHabit.getTargetCount() <= userHabit.getPositiveCount() - userHabit.getNegativeCount())
        || (habit.getType().name().equals("NEGATIVE") && userHabit.getTargetCount() <= userHabit.getNegativeCount())
        ||(habit.getType().name().equals("POSITIVE") && userHabit.getTargetCount() <= userHabit.getPositiveCount())){
            userHabit.setCompleted(true);
            habitHistory.setCompleted(true);
            habitDTO.setCompleted(true);
        }
        else{
            userHabit.setCompleted(false);
            habitHistory.setCompleted(false);
            habitDTO.setCompleted(false);
        }
        this.userHabitRepository.save(userHabit);
        this.habitHistoryRepository.save(habitHistory);

        if(habit.getChallenge() != null) {
            UserChallenge userChallenge = this.userChallengeRepository.findByUserAndChallenge(user, habit.getChallenge()).get();
            this.challengeProgressService.calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),today);
            this.challengeProgressService.recalculateUserChallengeProgress(userChallenge);
            this.challengeProgressService.updateChallengeStreak(userChallenge);
        }

        return habitDTO;
    }

    @Transactional
    public void resetHabit(){
        List<UserHabit> userHabits = this.userHabitRepository.findAll();
        LocalDate today = LocalDate.now();
        for(UserHabit userHabit : userHabits){
            HabitHistory habitHistory = this.habitHistoryRepository.findByUserHabitAndDate(userHabit,today)
                    .map(habitHis->{
                        habitHis.setPositiveCount(userHabit.getPositiveCount());
                        habitHis.setNegativeCount(userHabit.getNegativeCount());
                        return habitHis;
                    })
                    .orElseGet(()->new HabitHistory().builder()
                            .userHabit(userHabit)
                            .negativeCount(userHabit.getNegativeCount())
                            .positiveCount(userHabit.getPositiveCount())
                            .date(today)
                            .build()
                    );
            habitHistoryRepository.save(habitHistory);

            userHabit.setNegativeCount(0L);
            userHabit.setPositiveCount(0L);
            userHabit.setCompleted(false);
            this.userHabitRepository.save(userHabit);
        }
    }

    public List<HabitDTO> getHabitsByChallengeId(Long challengeId) {
        List<UserHabit> userHabits = userHabitRepository.findByChallenge_ChallengeId(challengeId);
        return userHabits.stream().map(userHabit -> HabitDTO.builder()
                .habitId(userHabit.getHabit().getHabitId())
                .title(userHabit.getHabit().getTitle())
                .description(userHabit.getHabit().getDescription())
                .type(userHabit.getHabit().getType())
                .difficulty(userHabit.getHabit().getDifficulty())
                .targetCount(userHabit.getHabit().getTargetCount())
                .challengeId(userHabit.getHabit().getChallenge() != null ? userHabit.getHabit().getChallenge().getChallengeId() : null)
                .build()).collect(Collectors.toList());
    }

    public void unlinkHabitFromChallenge(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Không tìm thây dữ liệu"));
        habit.setChallenge(null);
        habitRepository.save(habit);
    }
}
