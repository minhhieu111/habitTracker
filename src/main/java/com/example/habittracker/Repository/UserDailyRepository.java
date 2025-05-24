package com.example.habittracker.Repository;

import com.example.habittracker.Domain.Daily;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDailyRepository extends JpaRepository<UserDaily,Long> {
    Optional<UserDaily> findByUserAndDaily(User user, Daily daily);


    List<UserDaily> findByUser(User user);
}
