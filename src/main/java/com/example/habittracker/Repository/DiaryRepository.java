package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByUser(User user);

    @Query("SELECT ud.diaryId FROM Diary ud WHERE ud.user = :user AND ud.date = :date")
    List<Long> findIdByUserAndDate(@Param("user")User user, @Param("date") LocalDate date);
}
