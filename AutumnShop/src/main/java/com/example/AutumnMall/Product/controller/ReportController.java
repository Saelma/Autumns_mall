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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return reportService.findReportAll(loginUserDto.getMemberId(), addReportDto);
    }
}
