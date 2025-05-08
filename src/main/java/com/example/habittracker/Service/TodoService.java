package com.example.habittracker.Service;

import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getActiveTodos(User user) {
        Todo todo = this.todoRepository.findByUser(user);
        return todoRepository.findByTodoAndIsCompletedFalse(todo);
    }
}
