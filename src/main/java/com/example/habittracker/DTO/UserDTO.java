package com.example.habittracker.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String username;
    private String avatar;
    private String email;
    private String achieveTitle;
    private MultipartFile image;
    private String oldPassword;
    private String password;
    private String confirmPassword;
    private boolean justLoginWithGoogle;
}
