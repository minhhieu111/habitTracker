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
        User user = userRepository.findUserByUserName(userName).orElseThrow(()->new RuntimeException("Không tìm thấy người dùng!"));
        if (reward.getTitle().isEmpty()) {
            System.out.println(reward.getTitle());
            throw new RuntimeException("Thêm thất bại tiêu đề đang trống");
        }
        if (reward.getCoinCost() == null||reward.getCoinCost() < 0) {
            throw new RuntimeException("Thêm thất bại coin không hợp lệ!");
        }
        Reward rewardCreate = Reward.builder()
                .title(reward.getTitle())
                .description(reward.getDescription())
                .coinCost(reward.getCoinCost())
                .user(user)
                .build();

        this.rewardRepository.save(rewardCreate);
    }

    public Reward getRewardById (Long rewardId) {
        return this.rewardRepository.findById(rewardId).get();
    }

    public void updateReward(Reward reward) {
        Reward updateReward = this.rewardRepository.findById(reward.getRewardId()).get();
        if (updateReward == null) {
            throw new RuntimeException("Không thể cập nhật. Không tìm thấy phần thưởng");
        }
        if(reward.getTitle().isEmpty() || (reward.getCoinCost() == null || reward.getCoinCost() < 0)) {
            throw new RuntimeException("Không thể cập nhật. Yêu cầu không để trống tiêu đề và chi phí");
        }
        updateReward.setTitle(reward.getTitle());
        updateReward.setDescription(reward.getDescription());
        updateReward.setCoinCost(reward.getCoinCost());

        this.rewardRepository.save(updateReward);
    }

    public void deleteReward(Long rewardId) {
        Reward reward = this.rewardRepository.findById(rewardId).get();
        if(reward == null) {
            throw new RuntimeException("Không tìm thấy phần thưởng cần xóa!");
        }
        this.rewardRepository.delete(reward);
    }

    public Long exchangeReward(User user, Reward reward) {
        if(user==null || reward==null) {
            throw new RuntimeException("Không tìm thấy người dùng hoặc phần thưởng!");
        }
        if(user.getCoins()<reward.getCoinCost()) {
            throw new RuntimeException("Xu không đủ!");
        }
        Long exchange =reward.getCoinCost();
        user.setCoins(user.getCoins() - reward.getCoinCost());
        this.userRepository.save(user);
        return exchange;
    }
}
