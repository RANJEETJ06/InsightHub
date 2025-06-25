package com.insight.analysisinsights.controllers;

import com.insight.analysisinsights.models.InsightResult;
import com.insight.analysisinsights.repository.InsightResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightResultRepository repository;

    @GetMapping
    public List<InsightResult> getAll() {
        return repository.findAll();
    }
}
