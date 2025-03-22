package com.example.AutumnMall.Product.controller;

import com.example.AutumnMall.Product.domain.Report;
import com.example.AutumnMall.Product.domain.Review;
import com.example.AutumnMall.Product.dto.AddReportDto;
import com.example.AutumnMall.Product.service.ReportService;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public Report addReview(
            @IfLogin LoginUserDto loginUserDto,
            @RequestBody AddReportDto addReportDto
            ){
        if(loginUserDto.getMemberId() == null){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        return reportService.addReport(loginUserDto.getMemberId(), addReportDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Report> findReport(@RequestParam (required = false, defaultValue = "0") int page){
        int size = 10;

        return reportService.findReport(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Report> findReport(@PathVariable Long id){
        return reportService.findReport(id);
    }
}
