package com.insight.report.service;

import org.springframework.core.io.Resource;

import java.io.File;

public interface ControllerService {
    Resource buildDownloadResponse(File file);
}
