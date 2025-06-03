package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Challenge;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(User user);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.user = :user AND uc.challenge = :challenge")
    Optional<UserChallenge> findByUserAndChallenge (@Param("user")User user,@Param("challenge") Challenge challenge);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.challenge= :challenge")
    Optional<UserChallenge> findByChallenge(@Param("challenge") Challenge challenge);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.status = :status")
    List<UserChallenge> findByStatus(@Param("status") UserChallenge.Status status);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.status = :status AND uc.user=:user AND uc.isNotificationShown=false")
    List<UserChallenge> findByUserAndStatusAndIsNotificationShownFalse(@Param("user")User user,@Param("status") UserChallenge.Status status);
}
