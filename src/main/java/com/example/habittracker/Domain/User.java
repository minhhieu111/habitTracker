package com.example.habittracker.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @NotBlank(message = "Nhập tên người dùng")
    private String userName;

    @NotBlank(message = "Nhập mật khẩu")
    private String password;

    @NotBlank(message = "Nhập email")
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Long exp;
    private Long coins;
    private Long level;

    private String token;

    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "user")
    List<UserHabit> userHabits;

    @OneToMany(mappedBy = "user")
    List<UserDaily> userDailies;

    @OneToMany(mappedBy = "user")
    List<Todo> todos;

    @OneToMany(mappedBy = "user")
    List<UserChallenge> userChallenges;

    @OneToMany(mappedBy = "user")
    List<Reward> rewards;


    @OneToMany(mappedBy = "user")
    List<UserAchievement> userAchievements;


    @OneToMany(mappedBy = "user")
    List<Diary> diaries;


    public enum Role{
        ADMIN,USER
    }
}
