package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyDailyId;
    private LocalDate date;
    private boolean isCompleted;
    private Long expEarned;
    private Long streak;

    @ManyToOne()
    @JoinColumn(name = "user_daily_id")
    private UserDaily userDaily;
}
