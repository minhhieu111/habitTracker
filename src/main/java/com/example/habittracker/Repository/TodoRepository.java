package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT th from Todo th WHERE th.user = :user")
    Todo findByUser(@Param("user") User user);

    @Query("SELECT th FROM TodoHistory th WHERE th.todo = :todo AND th.isCompleted = false")
    List<Todo> findByTodoAndIsCompletedFalse(@Param("todo")Todo todo);


}
