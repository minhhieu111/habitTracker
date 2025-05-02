package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Challenge;
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
}
