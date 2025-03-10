package com.example.AutumnMall.Member.repository;

import com.example.AutumnMall.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(Long memberId);

    boolean existsByEmail(String email);
}
