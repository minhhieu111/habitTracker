package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Daily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long DailyId;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    @Enumerated(EnumType.STRING)
    private RepeatFrequency repeatFrequency;
    private LocalDateTime createAt = LocalDateTime.now();

    @OneToMany(mappedBy = "daily")
    private List<UserDaily> userDailies;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    enum RepeatFrequency {
        DAILY, WEEKLY, MONTHLY
    }
}
