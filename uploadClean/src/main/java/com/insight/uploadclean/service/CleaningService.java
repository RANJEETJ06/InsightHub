package com.insight.uploadclean.service;

import java.util.Map;

public interface CleaningService {
    Map<String, Object> cleanAndProcess(String fileId);
}
