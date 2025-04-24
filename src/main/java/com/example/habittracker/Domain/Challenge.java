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
    private Long day;
    @Enumerated(EnumType.STRING)
    private Visibility isPublic;
    private Long participantCount;


    @OneToMany(mappedBy = "challenge")
    List<UserChallenge> userChallenges;

    enum Visibility {
        PUBLIC, PRIVATE
    }


}
