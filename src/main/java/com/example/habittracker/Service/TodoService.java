package com.example.habittracker.Service;

import com.example.habittracker.DTO.TodoDTO;
import com.example.habittracker.DTO.TodoSubtaskDTO;
import com.example.habittracker.Domain.Todo;
import com.example.habittracker.Domain.TodoSubtask;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.TodoRepository;
import com.example.habittracker.Repository.TodoSubTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoSubTaskRepository todoSubTaskRepository;

    public TodoService(TodoRepository todoRepository, TodoSubTaskRepository todoSubTaskRepository) {
        this.todoRepository = todoRepository;
        this.todoSubTaskRepository = todoSubTaskRepository;
    }

    @Transactional
    public List<Todo> getActiveTodos(User user) {
        return todoRepository.findByUserAndIsCompletedFalse(user);
    }

    @Transactional
    public TodoDTO getUpdateTodo(User user, Long todoId){
        Todo todo = this.todoRepository.findByUserAndTodoId(user, todoId).orElseThrow(()->new RuntimeException("Không tìm thấy việc cần làm!"));
        List<TodoSubtaskDTO> todoSubtaskDTOs = todo.getTodoSubTasks().stream().map(todoSubtask ->
            TodoSubtaskDTO.builder()
                    .todoSubtaskId(todoSubtask.getTodoSubtaskId())
                    .title(todoSubtask.getTitle())
                    .isCompleted(todoSubtask.isCompleted())
                    .build()
        ).collect(Collectors.toList());

        return TodoDTO.builder()
                .todoId(todo.getTodoId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .difficulty(todo.getDifficulty())
                .todoSubtasks(todoSubtaskDTOs)
                .build();
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
                this.todoSubTaskRepository.save(subtask);
                todo.getTodoSubTasks().add(subtask);
            }
        }
        this.todoRepository.save(todo);
    }

    @Transactional
    public void updateTodo(TodoDTO todoDTO,User user, Long id) {
        Todo todo = this.todoRepository.findByUserAndTodoId(user, id).orElseThrow(()->new RuntimeException("Không tìm thấy việc cần làm!"));

        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDifficulty(todoDTO.getDifficulty());

        this.todoSubTaskRepository.deleteAll();
        for (int i = 0; i < todoDTO.getSubtasks().size(); i++) {
            String subtaskTitle = todoDTO.getSubtasks().get(i);
            if (subtaskTitle != null && !subtaskTitle.trim().isEmpty()) {
                TodoSubtask subtask = TodoSubtask.builder()
                        .title(subtaskTitle)
                        .isCompleted(false)
                        .todo(todo)
                        .build();

                this.todoSubTaskRepository.save(subtask);
                todo.getTodoSubTasks().add(subtask);
            }
        }

        todoRepository.save(todo);
    }

    @Transactional
    public void deleteTodo(User user, Long todoId){
        Todo todo = this.todoRepository.findByUserAndTodoId(user, todoId).orElseThrow(()->new RuntimeException("Không tìm thấy việc cần làm!"));

        this.todoSubTaskRepository.deleteAll();
        this.todoRepository.delete(todo);
    }
}
