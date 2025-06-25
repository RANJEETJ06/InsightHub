package com.insight.uploadclean.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileStorageService {
    List<Map<String, Object>> storeFiles(List<MultipartFile> files);
}