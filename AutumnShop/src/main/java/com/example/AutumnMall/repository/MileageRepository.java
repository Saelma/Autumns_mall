package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import com.example.AutumnMall.domain.MileageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
    Page<Mileage> findByMember(Member member, Pageable pageable);

    // 소멸 마일리지 조회
    List<Mileage> findByMemberAndExpirationDateBeforeAndType(Member member, LocalDate expirationDate, MileageType type);

    List<Mileage> findByMemberAndType(Member member, MileageType type);
}
