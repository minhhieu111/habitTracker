package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userChallengeId;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long streak;
    private Long daysSinceStart;

    private Double progress;
    private Long bestStreak;
    private Long totalCompletedTasks;
    private Long totalExpectedTasks;
    private Long completedTasks;
    private Long skippedTasks;

    @ElementCollection
    @CollectionTable(name = "user_challenge_completed_dates",
            joinColumns = @JoinColumn(name = "user_challenge_id"))
    @Column(name = "completed_date")
    private List<LocalDate> completedTasksList;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "userChallenge")
    private List<UserChallengeDailyProgress> dailyProgresses;

    public enum Status{
        ACTIVE,COMPLETE
    }
}
