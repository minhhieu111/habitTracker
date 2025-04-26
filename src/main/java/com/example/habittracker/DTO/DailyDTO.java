package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Daily;
import lombok.*;

import java.time.LocalDate;
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
    private Daily.Difficulty difficulty;
    private Daily.RepeatFrequency repeatFrequency;
    private Integer repeatEvery;
    private Set<Daily.DayOfWeek> repeatDays = new HashSet<>();
    private Set<Integer> repeatMonthDays = new HashSet<>();

    private final Daily.Difficulty[] difficultyOptions = Daily.Difficulty.values();
    private final Daily.RepeatFrequency[] repeatFrequencyOptions = Daily.RepeatFrequency.values();
    private final Daily.DayOfWeek[] dayOfWeekOptions = Daily.DayOfWeek.values();
}
