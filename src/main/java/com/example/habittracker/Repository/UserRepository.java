package com.example.habittracker.Repository;

import com.example.habittracker.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsUserByEmail(String email);

    boolean existsUserByUserName(String userName);

    Optional<User> findUserByUserName(String userName);
}
