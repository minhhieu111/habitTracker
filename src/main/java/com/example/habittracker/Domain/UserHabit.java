package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserHabit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userHabitId;
    private Long currentCount;
    private Long targetCount;
    private String EmailMessage;
    private LocalDateTime timeSendEmail;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    @OneToMany(mappedBy = "userHabit")
    private List<HabitHistory> habitHistories;
}
