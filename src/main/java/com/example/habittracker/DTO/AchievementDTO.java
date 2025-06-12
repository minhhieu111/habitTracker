package com.example.habittracker.DTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementDTO {
    private String achievementTitle;
    private String achievementDescription;
    private Long bonusChallenge;
    private Long bonusTask;
}
