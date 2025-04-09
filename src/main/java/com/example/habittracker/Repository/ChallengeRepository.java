package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
//    List<Challenge> findChallengeByUsers_Username(String username);
}
