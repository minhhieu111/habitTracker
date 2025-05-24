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
public class Daily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long DailyId;
    private String title;
    private String description;
    private LocalDate createAt = LocalDate.now();
    private Long challengeId;

    @OneToMany(mappedBy = "daily")
    private List<UserDaily> userDailies;

}
