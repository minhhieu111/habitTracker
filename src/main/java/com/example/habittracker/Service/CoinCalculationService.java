package com.example.habittracker.Service;

import com.example.habittracker.Domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class CoinCalculationService {
    private static final Map<Habit.Difficulty, Integer> BASE_COINS = Map.of(
            Habit.Difficulty.EASY, 10,
            Habit.Difficulty.MEDIUM, 20,
            Habit.Difficulty.HARD, 35
    );

    public long calculatePositiveHabitCoins(Habit habit, boolean isInChallenge) {
        int baseCoins = BASE_COINS.get(habit.getDifficulty());

        double challengeMultiplier = isInChallenge ? 1.3 : 1.0;

        int totalCoins = (int) ((baseCoins) * challengeMultiplier);
        return Math.min(totalCoins, 50);
    }

    public long calculateNegativeHabitCoins(Habit habit, long dailyNegativeCount, boolean isInChallenge) {
        int baseCoins = BASE_COINS.get(habit.getDifficulty());

        // Chỉ được xu nếu không vượt target trong ngày
        if (dailyNegativeCount >= habit.getTargetCount()) {
            return 0;
        }

        // Tính success rate dựa trên việc tránh được thói quen xấu
        double successRate = Math.max(0, 1.0 - (double)dailyNegativeCount / habit.getTargetCount());
        double challengeMultiplier = isInChallenge ? 1.3 : 1.0;

        int dailyCoins = (int)(baseCoins * successRate);
        int totalCoins = (int)((dailyCoins) * challengeMultiplier);

        return Math.min(totalCoins, 50);
    }

    public long calculateDailyCoins(UserDaily userDaily, boolean isInChallenge) {
        int baseCoins = BASE_COINS.get(convertToHabitDifficulty(userDaily.getDaily().getDifficulty(),null));
        long streakBonus = Math.min(userDaily.getStreak() / 7, 8);
        double challengeMultiplier = isInChallenge ? 1.3 : 1.0;

        int totalCoins = (int)((baseCoins + streakBonus) * challengeMultiplier);
        return Math.min(totalCoins, 60);
    }

    public long calculateTodoCoins(Todo todo) {
        int baseCoins = BASE_COINS.get(convertToHabitDifficulty(null,todo.getDifficulty()));

        // Subtask bonus
        int completedSubtasks = todo.getTodoSubTasks() != null ?
                todo.getTodoSubTasks().stream().mapToInt(sub -> sub.isCompleted() ? 1 : 0).sum() : 0;
        int subtaskBonus = completedSubtasks * 3;

        // Urgency bonus (hoàn thành trước deadline)
        int urgencyBonus = calculateUrgencyBonus(todo, baseCoins);

        int totalCoins = baseCoins + subtaskBonus + urgencyBonus;

        return Math.min(totalCoins, 80);
    }

    private int calculateUrgencyBonus(Todo todo, int baseCoins) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = todo.getExecution_date();

        if (deadline == null) return 0;

        long daysUntilDeadline = ChronoUnit.DAYS.between(today, deadline);

        if (daysUntilDeadline < 0) return 0;
        if (daysUntilDeadline == 0) return (int)(baseCoins * 0.3);
        if (daysUntilDeadline > 0) return (int)(baseCoins * 0.5);

        return 0;
    }

    public long calculateDiaryCoins(Diary diary) {
        int baseCoins = 12;

        // Quality bonus
        int qualityBonus = 0;

        if (diary.getImageUrl() != null && !diary.getImageUrl().isEmpty()) {
            qualityBonus = 5;
        }

        int totalCoins = baseCoins + qualityBonus;

        return Math.min(totalCoins, 25);
    }

    public long calculateChallengeCompletionReward(Challenge challenge, UserChallenge userChallenge, boolean expectCoin) {
        int baseReward = (int)(challenge.getDay() * 7); // Tăng base reward

        // Final completion rate
        double finalCompletionRate;
        int streakBonus;
        if(expectCoin){
            finalCompletionRate = userChallenge.getTotalExpectedTasks() > 0 ?
                    (double)userChallenge.getTotalExpectedTasks() / userChallenge.getTotalExpectedTasks() : 0;
            streakBonus = (int)(challenge.getDay() * 3);
        }else{
            finalCompletionRate = userChallenge.getTotalExpectedTasks() > 0 ?
                    (double)userChallenge.getTotalCompletedTasks() / userChallenge.getTotalExpectedTasks() : 0;
            streakBonus = (int)(userChallenge.getBestStreak() * 3);
        }


        // Performance multiplier
        double performanceMultiplier = 1.0;
        if (finalCompletionRate >= 0.95) performanceMultiplier = 2.0;
        else if (finalCompletionRate >= 0.85) performanceMultiplier = 1.7;
        else if (finalCompletionRate >= 0.75) performanceMultiplier = 1.4;
        else if (finalCompletionRate >= 0.6) performanceMultiplier = 1.1;

        return (int)(baseReward * performanceMultiplier) + streakBonus;
    }


    public long calculateDailyCompletionBonus(int completedTasks) {
        if (completedTasks >= 8) return 30; // Siêu productive
        if (completedTasks >= 6) return 20; // Rất productive
        if (completedTasks >= 4) return 15; // Productive
        if (completedTasks >= 2) return 8;  // Ổn định
        return 0;
    }

    public static Habit.Difficulty convertToHabitDifficulty(Daily.Difficulty dailyDifficulty, Todo.Difficulty todoDifficulty) {
        return Habit.Difficulty.valueOf(dailyDifficulty != null ? dailyDifficulty.name() : todoDifficulty.name());
    }

}
