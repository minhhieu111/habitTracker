package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Habit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitDTO {
    private Long habitId;
    private String title;
    private String description;
    private Habit.Difficulty difficulty;
    private Habit.HabitType type;
    private Long challengeId;
    private Long targetCount =0L;
    private Long userId;

    private final Habit.Difficulty[] habitDifficultiesOption = Habit.Difficulty.values();
    private final Habit.HabitType[] habitTypesOption = Habit.HabitType.values();
}
