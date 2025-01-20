package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Mileage;
import com.example.AutumnMall.domain.MileageType;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


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
                .type(MileageType.ADD)
                .description("마일리지 적립")
                .expirationDate(LocalDate.now().plusDays(3))
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
                .type(MileageType.MINUS)
                .description("마일리지 소모")
                .expirationDate(LocalDate.now())
                .build();

        member.setTotalMileage(member.getTotalMileage() - amount);
        mileageRepository.save(mileage);
    }

    @Transactional
    public void expireMileage(Long memberId) {
        LocalDate now = LocalDate.now();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 만료된 적립 마일리지 목록
        List<Mileage> expiredMileage = mileageRepository.findByMemberAndExpirationDateBeforeAndType(
                member, now, MileageType.ADD);

        // 만료된 마일리지 기록 추가 및 기존 마일리지 업데이트
        for (Mileage mileage : expiredMileage) {

            // 기존 마일리지 소멸 처리
            mileage.setType(MileageType.EXPIRATION);
            mileage.setDescription("마일리지 적립 후 소멸");

            mileageRepository.save(mileage);    // 기존 마일리지 업데이트
        }
    }

    @Transactional
    public void updateMileage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // `ADD`, `MINUS`, `EXPIRATION` 유형 모두 반영하여 총 마일리지 계산
        int addMileageTotal = mileageRepository.findByMemberAndType(member, MileageType.ADD).stream()
                .mapToInt(Mileage::getAmount)
                .sum();

        int minusMileageTotal = mileageRepository.findByMemberAndType(member, MileageType.MINUS).stream()
                .mapToInt(Mileage::getAmount)
                .sum();

        int expirationMileageTotal = mileageRepository.findByMemberAndType(member, MileageType.EXPIRATION).stream()
                .mapToInt(Mileage::getAmount)
                .sum();

        // 먼저 만료된 마일리지에서 사용 마일리지를 차감하고, 남은 게 있다면 적립 마일리지에서 차감
        int expminus = minusMileageTotal - expirationMileageTotal;
        if(expminus > 0)
            member.setTotalMileage(addMileageTotal - expminus);
        else
            member.setTotalMileage(addMileageTotal);
    }

    public Page<Mileage> getMileageHistory(Long memberId, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return mileageRepository.findByMember(member, pageable);
    }
}
