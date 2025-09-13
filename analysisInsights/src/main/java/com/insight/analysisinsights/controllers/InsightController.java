package com.insight.analysisinsights.controllers;

import com.insight.analysisinsights.models.InsightResult;
import com.insight.analysisinsights.models.ReportStatus;
import com.insight.analysisinsights.repository.InsightResultRepository;
import com.insight.analysisinsights.services.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightResultRepository repository;
    private final InsightService insightService;

    @GetMapping
    public List<InsightResult> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{filename}/status")
    public ResponseEntity<ReportStatus> getReportStatus(@PathVariable String filename) {
        String status = insightService.getReportStatus(filename);
        return ResponseEntity.ok(new ReportStatus(status));
    }
}
