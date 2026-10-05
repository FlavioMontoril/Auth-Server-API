package com.api.authserver.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService {

    // Injetado o caminho definido em application.properties
    @Value("${app.upload.dir}")
    private String uploadDir;

    public String uploadImg(MultipartFile multipartFile) {
        try {
            Path directory = Paths.get(uploadDir);
            Files.createDirectories(directory);

            String contentType = multipartFile.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException(
                        "O arquivo deve ser uma imagem");
            }

            if (multipartFile.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "A imagem não pode exceder 5MB");
            }

            String extension = "";

            String originalName = multipartFile.getOriginalFilename();

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;

            Path filePath = directory
                    .resolve(fileName)
                    .normalize();

            multipartFile.transferTo(filePath);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o upload do arquivo: " + e.getMessage());
        }
    }

    public void deleteImg(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path uploadPath = Paths
                    .get(uploadDir)
                    .toAbsolutePath()
                    .normalize();

            Path filePath = uploadPath
                    .resolve(fileName)
                    .normalize();

            if (!filePath.startsWith(uploadPath)) {
                throw new SecurityException("Caminho de arquivo inválido");
            }

            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erro ao excluir o arquivo: " + fileName,
                    e);
        }
    }

}
