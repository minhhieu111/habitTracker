package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;
    private String title;
    private String description;
    private Long requiredLevel;

    @ManyToMany(mappedBy = "achievements")
    private List<User> users;
}
