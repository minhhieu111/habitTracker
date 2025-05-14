package com.example.habittracker.Repository;

import com.example.habittracker.Domain.TodoSubtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoSubTaskRepository extends JpaRepository<TodoSubtask,Long> {

}
