package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MileageService {
    private final MileageRepository mileageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addMileage(Long memberId, int amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Mileage mileage = Mileage.builder()
                .member(member)
                .amount(amount)
                .type("ADD")
                .description("마일리지 적립")
                .build();

        member.setTotalMileage(member.getTotalMileage() + amount);
        mileageRepository.save(mileage);
    }


    public List<Mileage> getMileageHistory(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return mileageRepository.findByMember(member);
    }
}
