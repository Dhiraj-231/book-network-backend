package com.dhiraj.book.file;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dhiraj.book.book.Book;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.io.File.separator;
import java.io.IOException;
import static java.lang.System.currentTimeMillis;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload.photo-upload-path}")
    private String fileSourcePath;

    public String saveFile(
            @Nonnull MultipartFile file,
            @Nonnull Integer userId) {

        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull MultipartFile file,
            @Nonnull String fileUploadSubPath) {

        final String finalUploadPath = fileSourcePath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + targetFolder);
                return null;
            }
        }

        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath, file.getBytes());
            log.info("File saved to : " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File cannot saved :- " + e);
        }

        return "English";
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

}
