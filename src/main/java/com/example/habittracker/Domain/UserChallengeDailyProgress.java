package com.example.habittracker.Domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class UserChallengeDailyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Integer completionPercentage;

    @ManyToOne
    @JoinColumn(name = "user_challenge_id")
    private UserChallenge userChallenge;


}
