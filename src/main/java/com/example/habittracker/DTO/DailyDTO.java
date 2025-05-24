package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.UserDaily;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyDTO {
    private Long dailyId;
    private Long userId;
    private Long challengeId;
    private String title;
    private String description;
    private UserDaily.Difficulty difficulty;
    private UserDaily.RepeatFrequency repeatFrequency;
    private Integer repeatEvery;
    private Set<UserDaily.DayOfWeek> repeatDays = new HashSet<>();
    private Set<Integer> repeatMonthDays = new HashSet<>();
    private Long streak;
    private boolean isCompleted;

    private final UserDaily.Difficulty[] difficultyOptions = UserDaily.Difficulty.values();
    private final UserDaily.RepeatFrequency[] repeatFrequencyOptions = UserDaily.RepeatFrequency.values();
    private final UserDaily.DayOfWeek[] dayOfWeekOptions = UserDaily.DayOfWeek.values();
}
