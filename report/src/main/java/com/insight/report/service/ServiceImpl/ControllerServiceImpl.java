package com.insight.report.service.ServiceImpl;

import com.insight.report.exceptions.ResourceNotFoundException;
import com.insight.report.service.ControllerService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ControllerServiceImpl implements ControllerService {
    @Override
    public Resource buildDownloadResponse(File file) {
        if (!file.exists()) {
            throw new ResourceNotFoundException("file", file.getName(), file.getAbsolutePath());
        }
        return new FileSystemResource(file);
    }
}
