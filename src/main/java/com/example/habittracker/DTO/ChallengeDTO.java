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
    private Long day; // Số ngày thực hiện
    private Challenge.Visibility isPublic;
    private List<HabitDTO> habits; // Danh sách Habit
    private List<DailyDTO> dailies; // Danh sách Daily
}
