package com.example.habittracker.DTO;

import com.example.habittracker.Domain.Challenge;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeDTO {
    private Long challengeId;
    private String title;
    private String description;
    private LocalDate endDate;
    private Long day;
    private Challenge.Visibility isPublic;
    private List<HabitDTO> habits;
    private List<DailyDTO> dailies;

    private Double progress;
    private Long bestStreak;
    private Long totalCompletedTasks;
    private Long completedTasks;
    private Long skippedTasks;
    private List<LocalDate> completedTasksList;
    private List<DailyProgressDTO> dailyProgresses;
}
