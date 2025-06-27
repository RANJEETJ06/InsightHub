package com.insight.report.controller;

import com.insight.report.service.ControllerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;


@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportDownloadController {

    private final ControllerService controllerService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String id) {
        File pdf = new File("./reports/" + id + "/report.pdf");
        Resource resource=controllerService.buildDownloadResponse(pdf);
        return ResponseEntity.ok().body(resource);
    }
}
