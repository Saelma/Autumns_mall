package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MileageService {
    private final MileageRepository mileageRepository;
    private final MemberRepository memberRepository;

    // addMileage와 minusMileage를 합칠 수 있을 것 같음
    // type을 받아서 type이 ADD면 addMileage를, type이 MINUS면 minusMileage를 실행하게 하면 될 것 같음.

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

    @Transactional
    public void minusMileage(Long memberId, int amount){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Mileage mileage = Mileage.builder().
                member(member)
                .amount(amount)
                .type("MINUS")
                .description("마일리지 소모")
                .build();

        member.setTotalMileage(member.getTotalMileage() - amount);
        mileageRepository.save(mileage);
    }


    public Page<Mileage> getMileageHistory(Long memberId, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return mileageRepository.findByMember(member, pageable);
    }
}
