package com.example.habittracker.Service;

import com.example.habittracker.DTO.Register;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(Register user) {
        if (!user.getPassword().equals(user.getConfirmPassword())){
            throw new RuntimeException("Passwords do not match");
        }
        User newUser = User.builder()
                .userName(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .role(User.Role.USER)
                .exp(0L)
                .coins(0L)
                .level(1L)
                .build();
        this.userRepository.save(newUser);
    }
    public boolean existsByEmail(String email){

        return this.userRepository.existsUserByEmail(email);
    }
    public boolean existsByUsername(String username){
        return this.userRepository.existsUserByUserName(username);
    }
}
