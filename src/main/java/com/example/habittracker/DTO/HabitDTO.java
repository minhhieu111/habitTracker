package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Habit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HabitDTO {
    private String title;
    private String description;
    private Habit.Difficulty difficulty;
    private Habit.HabitType type;
    private Long challengeId;
    private Long targetCount =0L;
    private Long userId;
}
