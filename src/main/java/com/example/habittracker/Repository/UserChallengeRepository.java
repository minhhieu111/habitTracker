package com.example.habittracker.Repository;

import com.example.habittracker.Domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
}
