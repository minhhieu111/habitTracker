package com.example.habittracker.Service;

import com.example.habittracker.Domain.Reward;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.RewardRepository;
import com.example.habittracker.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RewardService {
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;

    public RewardService(RewardRepository rewardRepository, UserRepository userRepository) {
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
    }

    public void save(Reward reward, String userName) {
        User user = userRepository.findUserByUserName(userName);
        if(user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
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
                .user(user)
                .build();

        this.rewardRepository.save(rewardCreate);
    }
}
