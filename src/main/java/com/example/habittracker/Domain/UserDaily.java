package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.config.Task;

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
    private Long streak;
    private String EmailMessage;
    private LocalDateTime timeSendEmail;
    private LocalTime executionTime;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "daily_id")
    private Daily daily;

    @OneToMany(mappedBy = "userDaily")
    private List<DailyHistory> dailyHistories;
}
