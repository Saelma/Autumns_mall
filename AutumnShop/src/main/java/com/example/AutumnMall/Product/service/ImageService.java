package com.example.AutumnMall.Product.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    public String saveImage(MultipartFile image) {
        try {
            // 파일 이름 생성
            String fileName = System.currentTimeMillis() + "-" + image.getOriginalFilename();

            // "uploads" 폴더가 없으면 생성
            if (!Files.exists(Paths.get("uploads"))) {
                Files.createDirectories(Paths.get("uploads"));
            }

            // 실제 파일 경로를 지정
            Path path = Paths.get(System.getProperty("user.dir")).resolve("uploads").resolve(fileName);


            // 파일을 로컬 파일 시스템에 저장
            image.transferTo(path.toFile());  // MultipartFile을 파일로 저장

            // 저장된 파일의 URL 반환
            return "http://localhost:8080/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
