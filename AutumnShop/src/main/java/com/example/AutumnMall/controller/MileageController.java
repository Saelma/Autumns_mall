package com.example.AutumnMall.controller;

import com.example.AutumnMall.domain.Mileage;
import com.example.AutumnMall.dto.AddMileageDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mileage")
@RequiredArgsConstructor
public class MileageController {
    @Autowired
    private final MileageService mileageService;

    @PostMapping("/add")
    public ResponseEntity<Void> addMileage(@IfLogin LoginUserDto loginUserDto, @RequestBody AddMileageDto addMileageDto){
        mileageService.addMileage(loginUserDto.getMemberId(),
                addMileageDto.getAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/minus")
    public ResponseEntity<Void> minusMileage(@IfLogin LoginUserDto loginUserDto, @RequestBody AddMileageDto addMileageDto){
        mileageService.minusMileage(loginUserDto.getMemberId(),
                addMileageDto.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<Mileage>> getMileageHistory(@PathVariable Long memberId){
        List<Mileage> history = mileageService.getMileageHistory(memberId);
        return ResponseEntity.ok(history);
    }
}
