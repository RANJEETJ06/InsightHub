package com.insight.uploadclean.controllers;

import com.insight.uploadclean.service.CleaningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clean")
@RequiredArgsConstructor
public class CleaningController {

    private final CleaningService cleaningService;

    @PostMapping("/{fileId}")
    public ResponseEntity<?> cleanFile(@PathVariable String fileId) {
        try {
            Map<String, Object> result = cleaningService.cleanAndProcess(fileId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
