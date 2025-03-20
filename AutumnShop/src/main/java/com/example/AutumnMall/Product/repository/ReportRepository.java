package com.example.AutumnMall.Product.repository;

import com.example.AutumnMall.Product.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByProductAndMember(Long productId, Long memberId);
}
