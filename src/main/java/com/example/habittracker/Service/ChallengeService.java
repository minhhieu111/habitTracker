package com.example.habittracker.Service;

import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public List<UserChallenge> getChallenges(Long userId) {
        return this.challengeRepository.findChallengeByUsers_Username(userId).get();
    }

}
