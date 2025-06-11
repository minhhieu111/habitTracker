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
    private final CoinCalculationService coinCalculationService;

    public HabitService(HabitRepository habitRepository, UserService userService, UserHabitRepository userHabitRepository, HabitHistoryRepository habitHistoryRepository, ChallengeRepository challengeRepository, UserChallengeRepository userChallengeRepository, ChallengeProgressService challengeProgressService, CoinCalculationService coinCalculationService) {
        this.habitRepository = habitRepository;
        this.userService = userService;
        this.userHabitRepository = userHabitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.challengeProgressService = challengeProgressService;
        this.coinCalculationService = coinCalculationService;
    }


//    public List<UserHabit> getUserHabits(User user) {
//        return this.userHabitRepository.findHabitsForUser(user.getUserId()).get();
//    }
    @Transactional
    public List<UserHabit> getUserHabits(User user) {
        return this.userHabitRepository.findHabitsForUser(user.getUserId()).stream()
                .filter(userHabit -> !userHabit.isUnavailable())
                .collect(Collectors.toList());
    }

    @Transactional
    public Habit getHabit(Long habitId) {
        return this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Không tìm thấy thói quen!"));
    }

    public List<UserHabit> getHabitIsUnComplete(User user){
        return this.userHabitRepository.findHabitsForUser(user.getUserId()).stream()
                .filter(userHabit -> !userHabit.isCompleted())
                .filter(userHabit-> !userHabit.isUnavailable())
                .collect(Collectors.toList());
    }

    public List<UserHabit> getUserHabitsChallenge(User user, Challenge challenge){
        return this.userHabitRepository.findByUserAndHabitChallengeAndUnavailableFalse(user, challenge);
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
        if(habitDTO.getType() == Habit.HabitType.NEGATIVE){
            userHabit.setCompleted(true);
        }else{
            userHabit.setCompleted(false);
        }
        this.userHabitRepository.save(userHabit);

        if (habitDTO.getType() == Habit.HabitType.NEGATIVE) {
            HabitHistory initialHistory = HabitHistory.builder()
                    .userHabit(userHabit)
                    .date(LocalDate.now())
                    .isCompleted(userHabit.isCompleted())
                    .positiveCount(0L)
                    .negativeCount(0L)
                    .coinEarned(0L)
                    .build();
            this.habitHistoryRepository.save(initialHistory);
        };
    }

    @Transactional
    public HabitDTO getUpdateHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = this.habitRepository.findById(habitId).orElseThrow(()->new RuntimeException("Lỗi khi chỉnh sửa thói quen!"));
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi lấy dữ liệu chỉnh sửa!"));

        boolean isPublic = false;
        if(habit.getChallenge()!=null){
            isPublic = habit.getChallenge().getIsPublic().equals(Challenge.Visibility.PUBLIC);
        }
        HabitDTO habitDTO = new HabitDTO().builder()
                .habitId(habit.getHabitId())
                .title(habit.getTitle())
                .description(habit.getDescription())
                .type(habit.getType())
                .difficulty(userHabit.getDifficulty())
                .targetCount(userHabit.getTargetCount())
                .isPublic(isPublic)
                .isInChallenge(userHabit.isInChallenge())
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
        Challenge challenge = null;
        if(habitDTO.getChallengeId()!=null){
            challenge = this.challengeRepository.findById(habitDTO.getChallengeId()).orElse(null);
        }

        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setChallenge(challenge);

        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi khi lưu dữ liệu chỉnh sửa!"));
        Habit.HabitType oldType = habit.getType();
        Habit.HabitType newType = habitDTO.getType();

        if(oldType != newType){
            if(newType == Habit.HabitType.NEGATIVE){
                userHabit.setCompleted(!(oldType == Habit.HabitType.BOTH && userHabit.getNegativeCount() >= userHabit.getTargetCount()));
                userHabit.setPositiveCount(0L);
                HabitHistory initialNegativeHistory = this.habitHistoryRepository.findDailyHistory(userHabit, LocalDate.now())
                        .orElseGet(() -> HabitHistory.builder()
                                .userHabit(userHabit)
                                .date(LocalDate.now())
                                .build());
                initialNegativeHistory.setCompleted(userHabit.isCompleted());
                initialNegativeHistory.setPositiveCount(0L);
                initialNegativeHistory.setNegativeCount(0L);
                initialNegativeHistory.setCoinEarned(0L);
                this.habitHistoryRepository.save(initialNegativeHistory);
            } else if (newType == Habit.HabitType.POSITIVE) {
                userHabit.setNegativeCount(0L);
                userHabit.setCompleted(oldType == Habit.HabitType.BOTH && userHabit.getPositiveCount() >= userHabit.getTargetCount());
            } else if (newType == Habit.HabitType.BOTH) {
                userHabit.setCompleted(oldType == Habit.HabitType.POSITIVE && userHabit.getPositiveCount() - userHabit.getNegativeCount() >= userHabit.getTargetCount());
            }
        }
        userHabit.setTargetCount(habitDTO.getTargetCount());
        userHabit.setDifficulty(habitDTO.getDifficulty());
        this.userHabitRepository.save(userHabit);

        habit.setType(habitDTO.getType());
        this.habitRepository.save(habit);
    }

    @Transactional
    public void deleteHabit(Long habitId, String username) {
        User user = this.userService.getUser(username);
        Habit habit = getHabit(habitId);
        UserHabit userHabit = this.userHabitRepository.findUserHabitByHabitAndUser(habit,user).orElseThrow(()->new RuntimeException("Lỗi xảy ra khi xóa thói quen!"));
        List<HabitHistory> habitHistories = this.habitHistoryRepository.findAllByUserHabit(userHabit);

        if(habit.getChallenge() != null){
            UserChallenge userChallenge = this.userChallengeRepository.findByUserAndChallenge(user,habit.getChallenge()).orElseThrow(()->new RuntimeException("Không tìm thấy dữ liệu để xóa!"));
            if(userChallenge.getStatus() == UserChallenge.Status.COMPLETE){
                userHabit.setUnavailable(true);
                this.userHabitRepository.save(userHabit);
            }else{
                this.habitHistoryRepository.deleteAll(habitHistories);
                this.userHabitRepository.delete(userHabit);
                this.habitRepository.delete(habit);
            }
        }else{
            this.habitHistoryRepository.deleteAll(habitHistories);
            this.userHabitRepository.delete(userHabit);
            this.habitRepository.delete(habit);
        }

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
                        .coinEarned(0L)
                        .build()
        );

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setPositiveCount(userHabit.getPositiveCount());
        habitDTO.setNegativeCount(userHabit.getNegativeCount());

        if((habit.getType().name().equals("BOTH") && userHabit.getTargetCount() <= userHabit.getPositiveCount() - userHabit.getNegativeCount())
        || (habit.getType().name().equals("NEGATIVE") && userHabit.getTargetCount() > userHabit.getNegativeCount())
        ||(habit.getType().name().equals("POSITIVE") && userHabit.getTargetCount() <= userHabit.getPositiveCount())){
            userHabit.setCompleted(true);
            habitHistory.setCompleted(true);
            habitDTO.setCompleted(true);

            if(!(habit.getType().name().equals("NEGATIVE")) && habitHistory.getCoinEarned()==0){
                Long coinEarn = this.coinCalculationService.calculatePositiveHabitCoins(habit, habit.getChallenge() != null);
                habitHistory.setCoinEarned(coinEarn);
                String message = this.userService.getCoinComplete(userHabit.getUser(),coinEarn);
                habitDTO.setUserCoinMessage(message);
                habitDTO.setCoinEarned(coinEarn);
            }
        }
        else{
            userHabit.setCompleted(false);
            habitHistory.setCompleted(false);
            habitDTO.setCompleted(false);

            if(habitHistory.getCoinEarned()>0){
                Long coinBack = this.habitHistoryRepository.findCoinEarnByUserHabitAndDate(userHabit,today);
                habitDTO.setCoinEarned(coinBack);
                String message = this.userService.getCoinComplete(userHabit.getUser(),-coinBack);
                habitDTO.setUserCoinMessage(message);
                habitHistory.setCoinEarned(0L);
            }
        }
        this.userHabitRepository.save(userHabit);
        this.habitHistoryRepository.save(habitHistory);

        if(habit.getChallenge() != null) {
            UserChallenge userChallenge = this.userChallengeRepository.findByUserAndChallenge(user, habit.getChallenge()).orElse(null);
            if(userChallenge != null && userChallenge.getStatus() == UserChallenge.Status.ACTIVE){
                this.challengeProgressService.calculateAndSaveDailyProgress(userChallenge.getUserChallengeId(),today);
                this.challengeProgressService.recalculateUserChallengeProgress(userChallenge);
                this.challengeProgressService.updateChallengeStreak(userChallenge,false);
            }
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
                            .coinEarned(0L)
                            .build()
                    );

            Long coinEarn = 0L;
            if(userHabit.getHabit().getType().name().equals("NEGATIVE")){
                coinEarn = this.coinCalculationService.calculateNegativeHabitCoins(userHabit.getHabit(),userHabit.getNegativeCount(), userHabit.getHabit().getChallenge() != null);
                this.userService.getCoinComplete(userHabit.getUser(),coinEarn);
                habitHistory.setCoinEarned(coinEarn);
            }
            habitHistoryRepository.save(habitHistory);

            userHabit.setNegativeCount(0L);
            userHabit.setPositiveCount(0L);
            userHabit.setCompleted(userHabit.getHabit().getType() == Habit.HabitType.NEGATIVE);
            this.userHabitRepository.save(userHabit);
        }
    }

    @Transactional
    public void setHabitHistoryNewDay(){
        List<UserHabit> userHabits = this.userHabitRepository.findAll();
        LocalDate today = LocalDate.now();
        for(UserHabit userHabit : userHabits){
            HabitHistory habitHistory = this.habitHistoryRepository.findByUserHabitAndDate(userHabit,today)
                    .orElseGet(()->new HabitHistory().builder()
                            .userHabit(userHabit)
                            .negativeCount(userHabit.getNegativeCount())
                            .positiveCount(userHabit.getPositiveCount())
                            .date(today)
                            .isCompleted(userHabit.isCompleted())
                            .coinEarned(0L)
                            .build()
                    );
            habitHistoryRepository.save(habitHistory);
        }
    }
//    @Transactional
//    public List<HabitDTO> getHabitsByUser_ChallengeId(User user,Long challengeId) {
//        List<UserHabit> userHabits = userHabitRepository.findByUser_ChallengeId(user,challengeId);
//        return userHabits.stream().map(userHabit -> HabitDTO.builder()
//                .habitId(userHabit.getHabit().getHabitId())
//                .title(userHabit.getHabit().getTitle())
//                .description(userHabit.getHabit().getDescription())
//                .type(userHabit.getHabit().getType())
//                .difficulty(userHabit.getHabit().getDifficulty())
//                .targetCount(userHabit.getHabit().getTargetCount())
//                .challengeId(userHabit.getHabit().getChallenge() != null ? userHabit.getHabit().getChallenge().getChallengeId() : null)
//                .build()).collect(Collectors.toList());
//    }

    @Transactional
    public List<HabitDTO> getHabitsByUser_ChallengeId(Long challengeId) {

        List<Habit> habits = habitRepository.findByChallenge(challengeId);
        return habits.stream().map(habit -> HabitDTO.builder()
                .habitId(habit.getHabitId())
                .title(habit.getTitle())
                .description(habit.getDescription())
                .type(habit.getType())
                .targetCount(habit.getTargetCount())
                .difficulty(habit.getDifficulty())
                .build()).collect(Collectors.toList());
    }


    @Transactional
    public void unlinkHabitFromChallenge(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Không tìm thây dữ liệu"));
        habit.setChallenge(null);
        habitRepository.save(habit);
    }
}
