package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.Diary;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDailyRepository extends JpaRepository<UserDaily,Long> {
    UserDaily findByUserAndDaily(User user, Daily daily);
}
