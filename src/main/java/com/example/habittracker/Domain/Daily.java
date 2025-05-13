package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private LocalDate createAt = LocalDate.now();
    private Integer repeatEvery;
    private Long challengeId;

    @ElementCollection
    @CollectionTable(name = "daily_repeat_days", joinColumns = @JoinColumn(name = "daily_id"))
    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> repeatDays;

    @ElementCollection
    @CollectionTable(name = "user_daily_repeat_month_days", joinColumns = @JoinColumn(name = "user_daily_id"))
    @Column(name = "day_of_month")
    private Set<Integer> repeatMonthDays;

    @OneToMany(mappedBy = "daily")
    private List<UserDaily> userDailies;


    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    public enum RepeatFrequency {
        DAILY, WEEKLY, MONTHLY
    }
    public enum DayOfWeek {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }
}
