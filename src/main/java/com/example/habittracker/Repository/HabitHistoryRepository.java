package com.example.habittracker.Repository;

import com.example.habittracker.Domain.HabitHistory;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {
    @Query("SELECT hs FROM HabitHistory hs WHERE hs.userHabit = :userHabit AND hs.date = :today")
    Optional<HabitHistory> findByUserHabitAndDate(@Param("userHabit")UserHabit userHabit,@Param("today") LocalDate date);

    @Query("SELECT hs FROM HabitHistory hs WHERE hs.userHabit = :userHabit")
    List<HabitHistory> findAllByUserHabit(@Param("userHabit") UserHabit userHabit);

    @Query("SELECT hh.userHabit.habit.habitId FROM HabitHistory hh WHERE hh.userHabit.user = :user AND hh.isCompleted = true AND DATE(hh.date) = :date")
    List<Long> findCompletedHabitIdsByUserAndDate(@Param("user") User user, @Param("date")LocalDate date);
}
