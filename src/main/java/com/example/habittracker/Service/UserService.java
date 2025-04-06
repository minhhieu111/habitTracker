package com.example.habittracker.Service;

import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username) {
        return this.userRepository.findUserByUserName(username);
    }
}
