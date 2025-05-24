package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDailyId;
    private Long streak = 0L;
    private String emailMessage;
    private LocalDateTime timeSendEmail;
    private LocalTime executionTime;
    private boolean isCompleted;
    private boolean isEnabled;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "daily_id")
    private Daily daily;

    @OneToMany(mappedBy = "userDaily")
    private List<DailyHistory> dailyHistories;

    @ElementCollection
    @CollectionTable(name = "user_daily_repeat_days", joinColumns = @JoinColumn(name = "user_daily_id"))
    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> repeatDays = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_daily_repeat_month_days", joinColumns = @JoinColumn(name = "user_daily_id"))
    @Column(name = "day_of_month")
    private Set<Integer> repeatMonthDays = new HashSet<>();

    private Integer repeatEvery;

    @Enumerated(EnumType.STRING)
    private RepeatFrequency repeatFrequency;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

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
