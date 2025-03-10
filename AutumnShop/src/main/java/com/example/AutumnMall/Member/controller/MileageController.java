package com.example.AutumnMall.Member.controller;

import com.example.AutumnMall.Member.domain.Mileage;
import com.example.AutumnMall.Member.dto.AddMileageDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Member.service.MileageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/expire")
    public ResponseEntity<Void> expireMileage(@IfLogin LoginUserDto loginUserDto){
        mileageService.expireMileage(loginUserDto.getMemberId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<Page<Mileage>> getMileageHistory(@IfLogin LoginUserDto loginUserDto,
                                                           @RequestParam int page,
                                                           @RequestParam int size){
        Page<Mileage> history = mileageService.getMileageHistory(loginUserDto.getMemberId(), PageRequest.of(page, size));
        return ResponseEntity.ok(history);
    }
}
