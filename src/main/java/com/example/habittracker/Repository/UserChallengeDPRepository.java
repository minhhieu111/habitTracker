package com.example.habittracker.Repository;

import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserChallengeDailyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserChallengeDPRepository extends JpaRepository<UserChallengeDailyProgress, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM UserChallengeDailyProgress dp WHERE dp.userChallenge=:userChallenge")
    void deleteAllByUserChallenge(@Param("userChallenge")UserChallenge userChallenge);
}
