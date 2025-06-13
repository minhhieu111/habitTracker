package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    @Query("SELECT a FROM Achievement a WHERE a.achievementId=:achievementId")
    Achievement getAchievementById(@Param("achievementId")Long achievementId);
}
