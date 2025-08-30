package com.insight.report.controller;

import com.insight.report.exceptions.ResourceNotFoundException;
import com.insight.report.service.ControllerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportDownloadController {

    private final ControllerService controllerService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String id) {
        File pdf = new File("./reports/" + id + "/report.pdf");

        if (!pdf.exists()) {
            throw new ResourceNotFoundException("PDF report not found", id, pdf.getAbsolutePath());
        }

        Resource resource = controllerService.buildDownloadResponse(pdf);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"" + pdf.getName() + "\"")
                .contentLength(pdf.length())
                .body(resource);
    }
    @GetMapping("/{id}/data")
    public ResponseEntity<List<Map<String, String>>> getData(@PathVariable String id) throws IOException {
        File folder = new File("./reports/" + id);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new ResourceNotFoundException("Folder not found", id, folder.getAbsolutePath());
        }

        File[] imageFiles = folder.listFiles(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                    name.endsWith(".gif") || name.endsWith(".bmp");
        });

        if (imageFiles == null || imageFiles.length == 0) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, String>> imagesWithTitles = new ArrayList<>();

        for (File image : imageFiles) {
            byte[] bytes = Files.readAllBytes(image.toPath());
            String base64 = "data:image/" + getFileExtension(image.getName()) + ";base64," + Base64.getEncoder().encodeToString(bytes);

            Map<String, String> obj = new HashMap<>();
            obj.put("title", image.getName());  // use filename as title
            obj.put("image", base64);
            imagesWithTitles.add(obj);
        }

        return ResponseEntity.ok(imagesWithTitles);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        File reportFolder = new File("./reports/" + id);
        if (!reportFolder.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            FileUtils.deleteDirectory(reportFolder); // Apache Commons IO
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
