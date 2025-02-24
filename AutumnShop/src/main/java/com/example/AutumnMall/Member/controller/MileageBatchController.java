package com.example.AutumnMall.Member.controller;

import com.example.AutumnMall.Member.service.MileageBatchService;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mileages/batch")
@RequiredArgsConstructor
@Slf4j
public class MileageBatchController {

    private final MileageBatchService mileageExpirationBatchService;

    @PostMapping("/expireMileage")
    public ResponseEntity<String> expireMileageBatch() {
        boolean success = mileageExpirationBatchService.runMileageExpiration();
        if (success) {
            return ResponseEntity.ok("Mileage expiration batch job started successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start mileage expiration batch job");
        }
    }
}