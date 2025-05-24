package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.UserHabit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitDTO {
    private Long habitId;
    private String title;
    private String description;
    private UserHabit.Difficulty difficulty;
    private Habit.HabitType type;
    private Long challengeId;
    private Long targetCount =0L;
    private Long userId;
    private Long negativeCount;
    private Long positiveCount;
    private boolean isCompleted =false;

    private final UserHabit.Difficulty[] habitDifficultiesOption = UserHabit.Difficulty.values();
    private final Habit.HabitType[] habitTypesOption = Habit.HabitType.values();
}
