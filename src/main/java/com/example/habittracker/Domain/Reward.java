package com.example.habittracker.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId;

    @NotBlank(message = "Nhập tiêu đề")
    private String title;
    private String description;

    @NotNull
    private Long coinCost;

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;

}
