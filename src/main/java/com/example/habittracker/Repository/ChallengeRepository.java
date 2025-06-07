package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Challenge;
import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.Habit;
import com.example.habittracker.Domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("SELECT uc FROM UserChallenge uc WHERE uc.user.userId = :userId ")
    Optional<List<UserChallenge>> findChallengeByUsers_Username(@Param("userId") Long userId);

    @Query("SELECT c.habits FROM Challenge c WHERE c.challengeId = :challengeId")
    List<Habit> findHabitsByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT c.dailies FROM Challenge c WHERE c.challengeId = :challengeId")
    List<Daily> findDailiesByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.challenge.creatorId = :userId AND uc.status = 'ACTIVE'")
    Optional<List<UserChallenge>> findUserChallengeOwner(@Param("userId")Long userId);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.user.userId = :userId ")
    Optional<List<UserChallenge>> findUnCompleteChallengeByUsers_Username(@Param("userId") Long userId);

}
