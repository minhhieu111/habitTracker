package com.example.habittracker.Service;

import com.example.habittracker.Auth.JwtUtil;
import com.example.habittracker.DTO.Login;
import com.example.habittracker.DTO.Register;
import com.example.habittracker.Domain.User;
import com.example.habittracker.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(Register register) {
        if (userRepository.existsUserByUserName(register.getUsername())) {
            throw new RuntimeException("Tên người dùng đã tồn tại ");
        }
        if (userRepository.existsUserByEmail(register.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (!register.getPassword().equals(register.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp");
        }

        User user = User.builder()
                .userName(register.getUsername())
                .password(passwordEncoder.encode(register.getPassword()))
                .email(register.getEmail())
                .role(User.Role.USER)
                .exp(0L)
                .coins(0L)
                .level(1L)
                .build();

        userRepository.save(user);
    }

    public User login(Login login) {
        User user = userRepository.findUserByUserName(login.getUserName());
        if (user == null) {
            throw new RuntimeException("Tên đăng nhập không đúng!");
        }
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        String token = jwtUtil.generateToken(user.getUserName(), user.getRole().toString());
        user.setToken(token);
        userRepository.save(user);
        return user;
    }
}
