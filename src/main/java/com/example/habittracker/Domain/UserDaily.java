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
public class UserDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDailyId;
    private Long streak =0L;
    private String EmailMessage;
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
}
