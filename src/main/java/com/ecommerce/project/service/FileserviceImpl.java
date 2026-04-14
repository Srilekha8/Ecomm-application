package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileserviceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile productImage) throws IOException {
        String originalFileName = productImage.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String newFileName = UUID.randomUUID() + extension;

        Path dirPath = Paths.get(path).toAbsolutePath();
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(newFileName);
        Files.copy(productImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }
}
