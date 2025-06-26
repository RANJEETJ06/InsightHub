package com.insight.report.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@RestController
@RequestMapping("/api/report")
public class ReportDownloadController {

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String id) {
        File pdf = new File("./reports/" + id + "/report.pdf");
        return buildDownloadResponse(pdf, "application/pdf");
    }

    @GetMapping("/{id}/excel")
    public ResponseEntity<Resource> downloadExcel(@PathVariable String id) {
        File excel = new File("./reports/" + id + "/report.xlsx");
        return buildDownloadResponse(excel, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private ResponseEntity<Resource> buildDownloadResponse(File file, String mimeType) {
        try {
            if (!file.exists()) {
                System.err.println("❌ Report file not found: " + file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            Path path = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .body(resource);
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + file.getAbsolutePath());
            return ResponseEntity.internalServerError().build();
        }
    }
}
