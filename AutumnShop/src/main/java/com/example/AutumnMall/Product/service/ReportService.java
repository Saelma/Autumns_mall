package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Report;
import com.example.AutumnMall.Product.dto.AddReportDto;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.Product.repository.ReportRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Report addReport(Long memberId, AddReportDto addReportDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });
        Product product = productRepository.findByIdWithLock(addReportDto.getProductId())  // 비관적 락 적용된 조회
                .orElseThrow(() -> {
                    log.error("물품이 존재하지 않습니다. 물품Id: {}", addReportDto.getProductId());
                    return new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
                });

        Report report = Report.builder()
                .product(product)
                .member(member)
                .reason(addReportDto.getReason())
                .content(addReportDto.getContent())
                .seen(false)
                .build();

        log.info("회원이 물품에 신고를 등록했습니다. 회원Id: {}, 물품Id: {}", memberId, addReportDto.getProductId());
        return reportRepository.save(report);
    }

    public Page<Report> findReport(int page, int size){
        return reportRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<Report> findReport(Long reportId){
        return reportRepository.findById(reportId);
    }
}
