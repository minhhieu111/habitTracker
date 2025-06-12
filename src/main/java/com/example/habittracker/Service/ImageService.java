package com.example.habittracker.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    static final String UPLOAD_DIR = "src/main/resources/static";
    static final String UPLOAD_TARGET = "target/classes/static";

    @Transactional
    public String saveImage(MultipartFile image, String photoFolder) throws IOException {
        if (image == null || image.isEmpty()) {
            return null; // Không có ảnh được tải lên
        }

        // Tạo tên file duy nhất

        String relativePath = "/images/" + photoFolder + "/" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
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

    @Transactional
    public String saveImageFromUrl(String imageUrl, String photoFolder) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null; // Không có URL
        }

        // Tạo tên file duy nhất
        String relativePath = "/images/" + photoFolder + "/" + System.currentTimeMillis() + "_avatar.jpg";
        try {
            // Tải ảnh từ URL
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            byte[] imageBytes = inputStream.readAllBytes();

            // Lưu vào thư mục src/main/resources/static
            Path srcPath = Paths.get(UPLOAD_DIR + relativePath);
            Files.createDirectories(srcPath.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.write(srcPath, imageBytes);

            // Lưu vào thư mục target/classes/static (môi trường runtime)
            Path targetPath = Paths.get(UPLOAD_TARGET + relativePath);
            Files.createDirectories(targetPath.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.write(targetPath, imageBytes);

            // Trả về đường dẫn tương đối để lưu vào DB
            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu avatar từ URL: " + e.getMessage());
        }
    }
}
