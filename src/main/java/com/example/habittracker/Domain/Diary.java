package com.example.habittracker.Domain;

import com.example.habittracker.DTO.CompletedTasksConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "diary")
    List<UserHabit> userHabitList;

    @OneToMany(mappedBy = "diary")
    List<UserDaily> userDailyList;

    @OneToMany(mappedBy = "diary")
    List<Todo> todoList;

    @Convert(converter = CompletedTasksConverter.class)
    private List<Long> completedTaskIds;
}
