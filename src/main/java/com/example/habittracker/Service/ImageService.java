package com.example.habittracker.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    static final String UPLOAD_DIR = "src/main/resources/static";
    static final String UPLOAD_TARGET = "target/classes/static";

    public String saveImage(MultipartFile image, String photoFolder) throws IOException {
        if (image == null || image.isEmpty()) {
            return null; // Không có ảnh được tải lên
        }

        // Tạo tên file duy nhất

        String relativePath = "/images" + photoFolder + "/" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
        try{
            // Lưu vào thư mục src/main/resources/static
            Path srcPath = Paths.get(UPLOAD_DIR + relativePath);
            Files.createDirectories(srcPath.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.write(srcPath, image.getBytes());

            // Lưu vào thư mục target/classes/static (môi trường runtime)
            Path targetPath = Paths.get(UPLOAD_TARGET + relativePath);
            Files.createDirectories(targetPath.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.write(targetPath, image.getBytes());

            // Trả về đường dẫn tương đối để lưu vào DB
            return relativePath;
        }catch(IOException e){
            throw new RuntimeException("Lỗi khi lưu ảnh: "+e.getMessage());
        }

    }
}
