package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Daily;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyDTO {
    private int dailyId;
    private int userId;
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
