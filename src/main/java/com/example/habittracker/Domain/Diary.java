package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;
    private LocalDate date;
    private String content;
    private String imageUrl;

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;
}
