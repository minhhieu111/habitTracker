package com.example.habittracker.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryDTO {
    private Long diaryId;
    private LocalDate date;
    private String content;
    private MultipartFile image;
    private String imageUrl;
    private List<Long> completedTaskIds;

//        public static List<Difficulty> getDifficultiesOption() {
//            return Arrays.asList(Difficulty.values());
//        }
}
