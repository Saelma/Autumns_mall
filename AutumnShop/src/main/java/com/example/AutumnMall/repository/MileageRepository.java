package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MileageRepository extends JpaRepository<Mileage, Long> {
    List<Mileage> findByMember(Member member);
}
