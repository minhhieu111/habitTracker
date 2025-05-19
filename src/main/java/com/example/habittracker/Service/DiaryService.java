package com.example.habittracker.Service;

import com.example.habittracker.DTO.DiaryDTO;
import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final DailyHistoryRepository dailyRepositoryHistory;
    private final TodoHistoryRepository todoHistory;
    private final ImageService imageService;
    private String photoFolder = "diaries";


    public DiaryService(DiaryRepository diaryRepository, HabitHistoryRepository habitHistoryRepository, DailyHistoryRepository dailyRepositoryHistory, TodoHistoryRepository todoHistory, ImageService imageService) {
        this.diaryRepository = diaryRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.dailyRepositoryHistory = dailyRepositoryHistory;
        this.todoHistory = todoHistory;
        this.imageService = imageService;
    }

    public List<Diary> getDiariesByUser(User user) {
        return diaryRepository.findByUser(user);
    }

    public DiaryDTO getDiaryDTO(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));
        return DiaryDTO.builder()
                .diaryId(diary.getDiaryId())
                .date(diary.getDate())
                .content(diary.getContent())
                .imageUrl(diary.getImageUrl())
                .completedTaskIds(diary.getCompletedTaskIds())
                .build();
    }

    @Transactional
    public void saveDiary(DiaryDTO diaryDTO, MultipartFile image, User user) {
        LocalDate today = LocalDate.now(); // 19/05/2025
        Diary diary = Diary.builder()
                .date(today)
                .content(diaryDTO.getContent())
                .user(user)
                .userHabitList(new ArrayList<>())
                .userDailyList(new ArrayList<>())
                .todoList(new ArrayList<>())
                .completedTaskIds(new ArrayList<>())
                .build();

        try{
            if (image != null && !image.isEmpty()) {
                String filePath = imageService.saveImage(image,photoFolder);
                diary.setImageUrl(filePath);
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        diaryRepository.save(diary);
    }

    @Transactional
    public void updateDiary(DiaryDTO diaryDTO, MultipartFile image) {
        Diary diary = diaryRepository.findById(diaryDTO.getDiaryId())
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));

        diary.setContent(diaryDTO.getContent());
        diary.setCompletedTaskIds(diaryDTO.getCompletedTaskIds());

        try{
            if (image != null && !image.isEmpty()) {
                String filePath = imageService.saveImage(image,photoFolder);
                diary.setImageUrl(filePath);
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        diaryRepository.save(diary);
    }

    @Transactional
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));
        diaryRepository.delete(diary);
    }

    @Transactional
    public void updateDiaryTasks(Long diaryId, User user) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Nhật ký không tìm thấy"));
        LocalDate today = LocalDate.now();

        // Lấy tất cả task đã hoàn thành hôm nay
        List<Long> completedTaskIds = new ArrayList<>();
        completedTaskIds.addAll(this.habitHistoryRepository.findCompletedHabitIdsByUserAndDate(user, today));
        completedTaskIds.addAll(this.dailyRepositoryHistory.findCompletedDailyIdsByUserAndDate(user, today));
        completedTaskIds.addAll(this.todoHistory.findCompletedTodoIdsByUserAndDate(user, today));

        diary.setCompletedTaskIds(completedTaskIds);
        diaryRepository.save(diary);
    }
}
