package com.example.habittracker.Service;

import com.example.habittracker.DTO.TodoDTO;
import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.TodoSubtask;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional
    public List<Todo> getActiveTodos(User user) {
        return todoRepository.findByUserAndIsCompletedFalse(user);
    }

    @Transactional
    public void saveTodo(TodoDTO todoDTO, User user) {
        Todo todo = Todo.builder()
                .title(todoDTO.getTitle())
                .description(todoDTO.getDescription())
                .difficulty(todoDTO.getDifficulty())
                .user(user)
                .isCompleted(false)
                .todoSubTasks(new ArrayList<>())
                .build();

        // Thêm subtasks
        for (int i = 0; i < todoDTO.getSubtasks().size(); i++) {
            String subtaskTitle = todoDTO.getSubtasks().get(i);
            if (subtaskTitle != null && !subtaskTitle.trim().isEmpty()) {
                TodoSubtask subtask = TodoSubtask.builder()
                        .title(subtaskTitle)
                        .isCompleted(false)
                        .todo(todo)
                        .build();
                todo.getTodoSubTasks().add(subtask);
            }
        }

        todoRepository.save(todo);
    }
}
