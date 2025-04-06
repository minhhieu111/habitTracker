package com.example.habittracker.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long HistoryTodoId;
    private LocalDateTime date;
    private boolean isCompleted;
    private Long expEarned;

    @ManyToOne()
    @JoinColumn(name = "todo_id")
    private Todo todo;
}
