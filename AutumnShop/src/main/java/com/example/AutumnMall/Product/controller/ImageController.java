package com.example.AutumnMall.Product.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    // 파일 저장 디렉토리 (예: '/uploads' 폴더 경로)
    private static final String UPLOAD_DIR = "uploads";  // 실제 경로로 변경

    // 이미지 파일 제공을 위한 엔드포인트
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        // 요청된 파일 경로
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);

        // 파일을 Resource로 반환
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();  // 파일이 없으면 404 응답
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);  // 파일 내용 반환
    }
}
