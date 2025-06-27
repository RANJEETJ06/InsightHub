package com.insight.report.service.ServiceImpl;

import com.insight.report.service.ControllerService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ControllerServiceImpl implements ControllerService {
    @Override
    public Resource buildDownloadResponse(File file) {
        try {
            if (!file.exists()) {
                System.err.println("❌ Report file not found: " + file.getAbsolutePath());
                return null;
            }

            Path path = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return resource;
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + file.getAbsolutePath());
            return null;
        }
    }
}
