package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private Visibility isPublic;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long participantCount;
    private Long streak;
    private Long bestStreak;

    @ManyToMany(mappedBy = "challenges")
    private List<User> users;

    enum Visibility {
        PUBLIC, PRIVATE
    }
    public enum Status{
        ACTIVE,COMPLETE
    }

}
