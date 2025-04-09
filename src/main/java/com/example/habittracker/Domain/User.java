package com.example.habittracker.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Long exp;
    private Long coins;
    private Long level;

    private String token;

    @OneToMany(mappedBy = "user")
    List<UserHabit> userHabits;

    @OneToMany(mappedBy = "user")
    List<UserDaily> userDailies;

    @OneToMany(mappedBy = "user")
    List<Todo> todos;

    @ManyToMany
    @JoinTable(name = "user_challenge",
    joinColumns = {@JoinColumn(name = "user_id")},
    inverseJoinColumns = {@JoinColumn(name = "challenge_id")})
    List<Challenge> challenges;

    @OneToMany(mappedBy = "user")
    List<Reward> rewards;


    @ManyToMany
    @JoinTable(name = "user_achievement",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "achievement_id")})
    List<Achievement> achievements;


    @OneToMany(mappedBy = "user")
    List<Diary> diaries;


    public enum Role{
        ADMIN,USER
    }
}
