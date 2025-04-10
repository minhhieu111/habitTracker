package com.example.habittracker.Service;

import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Repository.RewardRepository;
import org.springframework.stereotype.Service;

@Service
public class RewardService {
    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public void save(Reward reward) {
        if (reward.getTitle().isEmpty()) {
            throw new RuntimeException("Tiêu đề đang trống");
        }
        if (reward.getCoinCost() == null||reward.getCoinCost() < 0) {
            throw new RuntimeException("Coin không hợp lệ!");
        }
        Reward rewardCreate = Reward.builder()
                .title(reward.getTitle())
                .description(reward.getDescription())
                .coinCost(reward.getCoinCost())
                .build();

        this.rewardRepository.save(rewardCreate);
    }
}
