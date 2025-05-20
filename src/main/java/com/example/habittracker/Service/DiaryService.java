package com.example.habittracker.Service;

import com.example.habittracker.DTO.DiaryDTO;
import com.example.habittracker.DTO.TaskDTO;
import com.example.habittracker.Domain.*;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final DailyHistoryRepository dailyHistoryRepository;
    private final TodoHistoryRepository todoHistoryRepository;
    private final ImageService imageService;
    private String photoFolder = "diaries";
    private final UserHabitRepository userHabitRepository;
    private final UserDailyRepository userDailyRepository;
    private final TodoRepository todoRepository;


    public DiaryService(DiaryRepository diaryRepository, HabitHistoryRepository habitHistoryRepository, DailyHistoryRepository dailyHistoryRepository, TodoHistoryRepository todoHistoryRepository, ImageService imageService, UserHabitRepository userHabitRepository, UserDailyRepository userDailyRepository, TodoRepository todoRepository) {
        this.diaryRepository = diaryRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.dailyHistoryRepository = dailyHistoryRepository;
        this.todoHistoryRepository = todoHistoryRepository;
        this.imageService = imageService;
        this.userHabitRepository = userHabitRepository;
        this.userDailyRepository = userDailyRepository;
        this.todoRepository = todoRepository;
    }

    public List<Diary> getDiariesByUser(User user) {
        return diaryRepository.findByUser(user);
    }

    public DiaryDTO getDiaryDTO(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));

        List<TaskDTO> completedTasks = new ArrayList<>();

        // Lấy từ userHabitList
        for (UserHabit habit : diary.getUserHabitList()) {
            completedTasks.add(TaskDTO.builder()
                    .id(habit.getHabit().getHabitId())
                    .title(habit.getHabit().getTitle())
                    .build());
        }

        // Lấy từ userDailyList
        for (UserDaily daily : diary.getUserDailyList()) {
            completedTasks.add(TaskDTO.builder()
                    .id(daily.getDaily().getDailyId())
                    .title(daily.getDaily().getTitle())
                    .build());
        }

        // Lấy từ todoList
        for (Todo todo : diary.getTodoList()) {
            completedTasks.add(TaskDTO.builder()
                    .id(todo.getTodoId())
                    .title(todo.getTitle())
                    .build());
        }

        return DiaryDTO.builder()
                .diaryId(diary.getDiaryId())
                .date(diary.getDate())
                .content(diary.getContent())
                .imageUrl(diary.getImageUrl())
                .completedTasks(completedTasks)
                .build();
    }

    @Transactional
    public void saveDiary(DiaryDTO diaryDTO, MultipartFile image, User user) {
        LocalDate today = LocalDate.now();
        Diary diary = Diary.builder()
                .date(today)
                .content(diaryDTO.getContent())
                .user(user)
                .userHabitList(new ArrayList<>())
                .userDailyList(new ArrayList<>())
                .todoList(new ArrayList<>())
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                String filePath = imageService.saveImage(image, photoFolder);
                diary.setImageUrl(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage(), e);
            }
        }

        // Lấy và gán các task hoàn thành hôm nay
        List<Long> habitIds = habitHistoryRepository.findCompletedHabitIdsByUserAndDate(user, today);
        List<Long> dailyIds = dailyHistoryRepository.findCompletedDailyIdsByUserAndDate(user, today);
        List<Long> todoIds = todoHistoryRepository.findCompletedTodoIdsByUserAndDate(user, today);

        List<UserHabit> completedHabits = habitIds.isEmpty() ? new ArrayList<>() : userHabitRepository.findAllById(habitIds);
        List<UserDaily> completedDailies = dailyIds.isEmpty() ? new ArrayList<>() : userDailyRepository.findAllById(dailyIds);
        List<Todo> completedTodos = todoIds.isEmpty() ? new ArrayList<>() : todoRepository.findAllById(todoIds);

        for (UserHabit habit : completedHabits) {
            habit.setDiary(diary);
            diary.getUserHabitList().add(habit);
        }
        for (UserDaily daily : completedDailies) {
            daily.setDiary(diary);
            diary.getUserDailyList().add(daily);
        }
        for (Todo todo : completedTodos) {
            todo.setDiary(diary);
            diary.getTodoList().add(todo);
        }

        diaryRepository.save(diary);
    }

    @Transactional
    public void updateDiary(DiaryDTO diaryDTO, MultipartFile image) {
        Diary diary = diaryRepository.findById(diaryDTO.getDiaryId())
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));

        // Cập nhật nội dung
        diary.setContent(diaryDTO.getContent());

        // Xử lý ảnh
        if (image != null && !image.isEmpty()) {
            try {
                String filePath = imageService.saveImage(image, photoFolder);
                diary.setImageUrl(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi cập nhật ảnh: " + e.getMessage(), e);
            }
        }

        // Cập nhật danh sách task (tùy chọn)
        LocalDate today = LocalDate.now(); // 21/05/2025
        List<Long> habitIds = habitHistoryRepository.findCompletedHabitIdsByUserAndDate(diary.getUser(), today);
        List<Long> dailyIds = dailyHistoryRepository.findCompletedDailyIdsByUserAndDate(diary.getUser(), today);
        List<Long> todoIds = todoHistoryRepository.findCompletedTodoIdsByUserAndDate(diary.getUser(), today);

        List<UserHabit> completedHabits = habitIds.isEmpty() ? new ArrayList<>() : userHabitRepository.findAllById(habitIds);
        List<UserDaily> completedDailies = dailyIds.isEmpty() ? new ArrayList<>() : userDailyRepository.findAllById(dailyIds);
        List<Todo> completedTodos = todoIds.isEmpty() ? new ArrayList<>() : todoRepository.findAllById(todoIds);

        // Xóa task cũ
        diary.getUserHabitList().clear();
        diary.getUserDailyList().clear();
        diary.getTodoList().clear();

        // Gán task mới
        for (UserHabit habit : completedHabits) {
            diary.getUserHabitList().add(habit);
        }
        for (UserDaily daily : completedDailies) {
            diary.getUserDailyList().add(daily);
        }
        for (Todo todo : completedTodos) {
            diary.getTodoList().add(todo);
        }

        // Lưu thay đổi
        diaryRepository.save(diary);
    }

    @Transactional
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));
        diaryRepository.delete(diary);
    }

    @Transactional
    public DiaryDTO updateDiaryTasks(Long diaryId, User user) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));
        LocalDate today = LocalDate.now();

        // Lấy tất cả task đã hoàn thành hôm nay
        // Lấy tất cả ID của các task đã hoàn thành hôm nay
        List<Long> habitIds = habitHistoryRepository.findCompletedHabitIdsByUserAndDate(user, today);
        List<Long> dailyIds = dailyHistoryRepository.findCompletedDailyIdsByUserAndDate(user, today);
        List<Long> todoIds = todoHistoryRepository.findCompletedTodoIdsByUserAndDate(user, today);

        // Lấy các thực thể tương ứng bằng findAllById
        List<UserHabit> completedHabits = habitIds.isEmpty() ? new ArrayList<>() : userHabitRepository.findAllById(habitIds);
        List<UserDaily> completedDailies = dailyIds.isEmpty() ? new ArrayList<>() : userDailyRepository.findAllById(dailyIds);
        List<Todo> completedTodos = todoIds.isEmpty() ? new ArrayList<>() : todoRepository.findAllById(todoIds);

        // Cập nhật quan hệ trong Diary
        // Xóa các task cũ (nếu cần)
        diary.getUserHabitList().clear();
        diary.getUserDailyList().clear();
        diary.getTodoList().clear();

        // Gán các task đã hoàn thành vào danh sách
        for (UserHabit habit : completedHabits) {
            diary.getUserHabitList().add(habit);
        }
        for (UserDaily daily : completedDailies) {
            diary.getUserDailyList().add(daily);
        }
        for (Todo todo : completedTodos) {
            diary.getTodoList().add(todo);
        }

        diaryRepository.save(diary);
        return getDiaryDTO(diaryId);
    }


    public List<TaskDTO> getCompletedTasks(User user) {
        LocalDate today = LocalDate.now();

        // Lấy tất cả ID
        List<Long> habitIds = habitHistoryRepository.findCompletedHabitIdsByUserAndDate(user, today);
        List<Long> dailyIds = dailyHistoryRepository.findCompletedDailyIdsByUserAndDate(user, today);
        List<Long> todoIds = todoHistoryRepository.findCompletedTodoIdsByUserAndDate(user, today);

        List<TaskDTO> completedTasks = new ArrayList<>();

        // Lấy tất cả habits
        if (!habitIds.isEmpty()) {
            List<UserHabit> habits = userHabitRepository.findAllById(habitIds);
            habits.forEach(habit -> completedTasks.add(TaskDTO.builder()
                    .id(habit.getHabit().getHabitId())
                    .title(habit.getHabit().getTitle())
                    .build()));
        }

        // Lấy tất cả dailies
        if (!dailyIds.isEmpty()) {
            List<UserDaily> dailies = userDailyRepository.findAllById(dailyIds);
            dailies.forEach(daily -> completedTasks.add(TaskDTO.builder()
                    .id(daily.getDaily().getDailyId())
                    .title(daily.getDaily().getTitle())
                    .build()));
        }

        // Lấy tất cả todos
        if (!todoIds.isEmpty()) {
            List<Todo> todos = todoRepository.findAllById(todoIds);
            todos.forEach(todo -> completedTasks.add(TaskDTO.builder()
                    .id(todo.getTodoId())
                    .title(todo.getTitle())
                    .build()));
        }

        return completedTasks;
    }
}
