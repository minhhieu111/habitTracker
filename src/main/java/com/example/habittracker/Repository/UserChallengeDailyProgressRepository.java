package com.example.habittracker.Repository;

import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserChallengeDailyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface UserChallengeDailyProgressRepository extends JpaRepository<UserChallengeDailyProgress, Long> {

    @Query("SELECT dp FROM UserChallengeDailyProgress dp WHERE dp.userChallenge=:userchallenge AND dp.date=:date")
    Optional<UserChallengeDailyProgress> findByUserChallengeAndDate(@Param("userChallenge") UserChallenge userChallenge, @Param("date") LocalDate date);
}
