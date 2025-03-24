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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Report> findReport(@PathVariable Long id){
        Optional<Report> report = reportService.findReport(id);

        if(report.isPresent()){
            reportService.reportAsSeen(id);
            return ResponseEntity.ok(report.get());
        }else{
            throw new BusinessLogicException(ExceptionCode.REPORT_NOT_FOUND);
        }
    }

    // 관리자가 확인해야 할 미확인 신고 목록 가져오기
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Report>> getUnseenReports(@RequestParam (required = false, defaultValue = "0") int page){
        int size = 10;
        Page<Report> unseenReports = reportService.findBySeenFalse(page, size); // seen이 false인 신고만 가져오기
        return ResponseEntity.ok(unseenReports);
    }
}
