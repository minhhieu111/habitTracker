package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.TodoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TodoHistoryRepository extends JpaRepository<TodoHistory,Long> {
    @Query("SELECT th FROM TodoHistory th WHERE th.todo = :todoId AND th.date = :today")
    Optional<TodoHistory> findByDateAndTodoId(@Param("todoId")Todo todo, @Param("today")LocalDate today);
}
