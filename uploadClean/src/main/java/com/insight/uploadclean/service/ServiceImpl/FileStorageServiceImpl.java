package com.insight.uploadclean.service.ServiceImpl;

import com.insight.uploadclean.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public List<Map<String, Object>> storeFiles(List<MultipartFile> files) {
        List<Map<String, Object>> uploadedFileDetails = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("One of the files is empty");
            }

            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileId = UUID.randomUUID().toString();
                String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String storedFileName = fileId + fileExtension;

                Path targetLocation = uploadPath.resolve(storedFileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                Map<String, Object> fileMeta = new HashMap<>();
                fileMeta.put("fileId", fileId);
                fileMeta.put("originalFilename", originalFilename);
                fileMeta.put("storedFilename", storedFileName);
                fileMeta.put("uploadTime", Instant.now().toString());

                uploadedFileDetails.add(fileMeta);

            } catch (IOException e) {
                throw new RuntimeException("File upload failed: " + e.getMessage(), e);
            }
        }

        return uploadedFileDetails;
    }
}
