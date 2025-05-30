package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserHabitRepository extends JpaRepository<UserHabit,Long> {
    @Query("SELECT uh FROM UserHabit uh WHERE uh.user.userId = :userId")
    Optional<List<UserHabit>> findHabitsForUser(@Param("userId")Long userId);

    Optional<UserHabit> findUserHabitByHabitAndUser(Habit habit, User user);

    @Query("SELECT uh FROM UserHabit uh WHERE uh.habit.challenge.challengeId = :challengeId")
    List<UserHabit> findByChallenge_ChallengeId(@Param("challengeId")Long challengeId);
}
