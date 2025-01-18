package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
    Page<Mileage> findByMember(Member member, Pageable pageable);
}
